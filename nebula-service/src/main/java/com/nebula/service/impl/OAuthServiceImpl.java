package com.nebula.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.nebula.common.constant.RedisKey;
import com.nebula.common.exception.BusinessException;
import com.nebula.common.exception.ErrorCode;
import com.nebula.common.util.JwtUtil;
import com.nebula.config.config.JwtProperties;
import com.nebula.config.properties.GitHubOAuthProperties;
import com.nebula.model.dto.GitHubOAuthDTO;
import com.nebula.model.entity.SysUser;
import com.nebula.model.vo.GitHubTokenResponse;
import com.nebula.model.vo.GitHubUserInfo;
import com.nebula.model.vo.LoginVO;
import com.nebula.service.mapper.SysUserMapper;
import com.nebula.service.service.OAuthService;
import com.nebula.service.service.TokenStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * OAuth服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OAuthServiceImpl implements OAuthService {

    private final SysUserMapper sysUserMapper;
    private final PasswordEncoder passwordEncoder;
    private final TokenStorageService tokenStorageService;
    private final GitHubOAuthProperties gitHubOAuthProperties;
    private final JwtProperties jwtProperties;
    private final RestTemplate restTemplate;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LoginVO handleGitHubCallback(GitHubOAuthDTO oauthDTO) {
        log.info("开始处理GitHub OAuth回调, code长度: {}", oauthDTO.getCode() != null ? oauthDTO.getCode().length() : 0);

        try {
            // 1. 用code换取access_token
            String githubAccessToken = exchangeCodeForToken(oauthDTO.getCode());
            log.debug("成功获取GitHub access_token");

            // 2. 获取GitHub用户信息
            GitHubUserInfo githubUser = fetchGitHubUserInfo(githubAccessToken);
            log.info("成功获取GitHub用户信息, GitHub ID: {}, Login: {}", githubUser.getId(), githubUser.getLogin());

            // 3. 查找或创建用户
            SysUser sysUser = findOrCreateUser(githubUser);
            log.info("用户处理完成, 用户ID: {}, 用户名: {}", sysUser.getId(), sysUser.getUsername());

            // 4. 生成 Token 对
            TokenPair tokens = generateTokens(sysUser);

            // 5. 保存 Token 到 Redis
            long accessTtl = RedisKey.Token.getAccessTokenTtlSeconds(jwtProperties.getAccessTokenExpiration());
            long refreshTtl = RedisKey.Token.getRefreshTokenTtlSeconds(jwtProperties.getRefreshTokenExpiration());
            tokenStorageService.saveTokens(sysUser.getId(), tokens.accessToken(), tokens.refreshToken(), accessTtl, refreshTtl);

            // 6. 更新用户最后登录时间
            updateUserLoginInfo(sysUser);

            // 7. 构建返回结果
            LoginVO loginVO = buildLoginVO(sysUser, tokens.accessToken(), tokens.refreshToken());
            log.info("GitHub OAuth登录成功, 用户ID: {}", sysUser.getId());

            return loginVO;

        } catch (BusinessException e) {
            log.error("GitHub OAuth业务异常: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("GitHub OAuth处理失败", e);
            throw new BusinessException(ErrorCode.GITHUB_AUTH_FAILED, "GitHub登录失败，请稍后重试");
        }
    }

    /**
     * 用code换取access_token
     */
    private String exchangeCodeForToken(String code) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.set("Accept", "application/json");

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("client_id", gitHubOAuthProperties.getClientId());
            params.add("client_secret", gitHubOAuthProperties.getClientSecret());
            params.add("code", code);
            params.add("redirect_uri", gitHubOAuthProperties.getRedirectUri());

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

            ResponseEntity<GitHubTokenResponse> response = restTemplate.exchange(
                    gitHubOAuthProperties.getTokenUrl(),
                    HttpMethod.POST,
                    request,
                    GitHubTokenResponse.class
            );

            GitHubTokenResponse tokenResponse = response.getBody();
            if (tokenResponse == null || tokenResponse.getAccess_token() == null) {
                log.error("GitHub返回的access_token为空");
                throw new BusinessException(ErrorCode.GITHUB_AUTH_FAILED, "获取GitHub访问令牌失败");
            }

            return tokenResponse.getAccess_token();
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("换取GitHub access_token失败", e);
            throw new BusinessException(ErrorCode.GITHUB_AUTH_FAILED, "获取GitHub访问令牌失败");
        }
    }

    /**
     * 获取GitHub用户信息
     */
    private GitHubUserInfo fetchGitHubUserInfo(String accessToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            headers.set("Accept", "application/vnd.github.v3+json");

            HttpEntity<Void> request = new HttpEntity<>(headers);

            ResponseEntity<GitHubUserInfo> response = restTemplate.exchange(
                    gitHubOAuthProperties.getUserInfoUrl(),
                    HttpMethod.GET,
                    request,
                    GitHubUserInfo.class
            );

            GitHubUserInfo userInfo = response.getBody();
            if (userInfo == null) {
                log.error("GitHub返回的用户信息为空");
                throw new BusinessException(ErrorCode.OAUTH_USER_INFO_ERROR, "获取GitHub用户信息失败");
            }

            return userInfo;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("获取GitHub用户信息失败", e);
            throw new BusinessException(ErrorCode.OAUTH_USER_INFO_ERROR, "获取GitHub用户信息失败");
        }
    }

    /**
     * 生成 Token 对
     */
    private TokenPair generateTokens(SysUser sysUser) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", sysUser.getId());
        claims.put("email", sysUser.getEmail());
        claims.put("type", "access");

        String accessToken = JwtUtil.generateAccessToken(
                claims,
                jwtProperties.getSecret(),
                jwtProperties.getAccessTokenExpiration()
        );

        claims.put("type", "refresh");
        String refreshToken = JwtUtil.generateRefreshToken(
                claims,
                jwtProperties.getSecret(),
                jwtProperties.getRefreshTokenExpiration()
        );

        return new TokenPair(accessToken, refreshToken);
    }

    /**
     * 更新用户登录信息
     */
    private void updateUserLoginInfo(SysUser sysUser) {
        sysUser.setLastLoginAt(OffsetDateTime.now());
        sysUser.setLastSeenAt(OffsetDateTime.now());
        sysUserMapper.updateById(sysUser);
    }

    /**
     * 构建LoginVO
     */
    private LoginVO buildLoginVO(SysUser sysUser, String accessToken, String refreshToken) {
        LoginVO loginVO = new LoginVO();
        loginVO.setToken(accessToken);
        loginVO.setRefreshToken(refreshToken);
        loginVO.setUserInfo(buildUserInfo(sysUser));
        return loginVO;
    }

    /**
     * 构建用户信息
     */
    private LoginVO.UserInfo buildUserInfo(SysUser sysUser) {
        LoginVO.UserInfo userInfo = new LoginVO.UserInfo();
        userInfo.setId(sysUser.getId());
        userInfo.setUsername(sysUser.getUsername());
        userInfo.setEmail(sysUser.getEmail());
        userInfo.setNickname(sysUser.getNickname());
        return userInfo;
    }

    /**
     * Token 对（不可变记录类）
     */
    private record TokenPair(String accessToken, String refreshToken) {
    }

    @Override
    public String getGitHubAuthorizeUrl(String state) {
        try {
            String redirectUriEncoded = java.net.URLEncoder.encode(
                gitHubOAuthProperties.getRedirectUri(),
                    StandardCharsets.UTF_8
            );

            return String.format(
                "%s?client_id=%s&redirect_uri=%s&scope=user:email,read:user&state=%s",
                gitHubOAuthProperties.getAuthorizeUrl(),
                gitHubOAuthProperties.getClientId(),
                redirectUriEncoded,
                state
            );
        } catch (Exception e) {
            log.error("生成 GitHub授权URL失败", e);
            throw new BusinessException(ErrorCode.GITHUB_AUTH_FAILED, "生成授权URL失败");
        }
    }

    /**
     * 查找或创建用户
     */
    private SysUser findOrCreateUser(GitHubUserInfo githubUser) {
        // 尝试通过GitHub ID查找用户
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, "github_" + githubUser.getId());

        SysUser sysUser = sysUserMapper.selectOne(wrapper);

        if (sysUser == null) {
            // 创建新用户
            sysUser = new SysUser();
            sysUser.setUsername("github_" + githubUser.getId());
            sysUser.setPassword(passwordEncoder.encode(UUID.randomUUID().toString())); // 随机密码
            sysUser.setEmail(githubUser.getEmail() != null ? githubUser.getEmail() : "github_" + githubUser.getId() + "@github.com");
            sysUser.setNickname(githubUser.getName() != null ? githubUser.getName() : githubUser.getLogin());
            sysUser.setAvatarUrl(githubUser.getAvatarUrl());
            sysUser.setBio(githubUser.getBio());
            sysUser.setOnlineStatus("offline");
            sysUser.setLastSeenAt(java.time.OffsetDateTime.now());
            sysUserMapper.insert(sysUser);
        }

        return sysUser;
    }
}
