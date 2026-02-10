package com.nebula.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.nebula.common.util.JwtUtil;
import com.nebula.config.constant.RedisConstant;
import com.nebula.model.dto.GitHubOAuthDTO;
import com.nebula.model.entity.SysUser;
import com.nebula.model.entity.UserProfile;
import com.nebula.model.vo.GitHubTokenResponse;
import com.nebula.model.vo.GitHubUserInfo;
import com.nebula.model.vo.LoginVO;
import com.nebula.service.mapper.SysUserMapper;
import com.nebula.service.mapper.UserProfileMapper;
import com.nebula.service.service.OAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * OAuth服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OAuthServiceImpl implements OAuthService {

    @Value("${github.client.id}")
    private String clientId;

    @Value("${github.client.secret}")
    private String clientSecret;

    @Value("${github.redirect.uri}")
    private String redirectUri;

    private final SysUserMapper sysUserMapper;
    private final UserProfileMapper userProfileMapper;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    @Transactional
    public LoginVO handleGitHubCallback(GitHubOAuthDTO oauthDTO) {
        try {
            // 1. 用code换取access_token
            String tokenUrl = "https://github.com/login/oauth/access_token";
            Map<String, String> tokenRequest = new HashMap<>();
            tokenRequest.put("client_id", clientId);
            tokenRequest.put("client_secret", clientSecret);
            tokenRequest.put("code", oauthDTO.getCode());
            tokenRequest.put("redirect_uri", redirectUri);

            GitHubTokenResponse tokenResponse = restTemplate.postForObject(tokenUrl, tokenRequest, GitHubTokenResponse.class);

            if (tokenResponse == null || tokenResponse.getAccess_token() == null) {
                throw new RuntimeException("获取GitHub访问令牌失败");
            }

            // 2. 获取GitHub用户信息
            String userUrl = "https://api.github.com/user";
            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", "Bearer " + tokenResponse.getAccess_token());
            headers.put("Accept", "application/vnd.github.v3+json");

            org.springframework.http.HttpEntity<?> request = new org.springframework.http.HttpEntity<>(headers);
            GitHubUserInfo githubUser = restTemplate.exchange(userUrl, org.springframework.http.HttpMethod.GET, request, GitHubUserInfo.class).getBody();

            if (githubUser == null) {
                throw new RuntimeException("获取GitHub用户信息失败");
            }

            // 3. 查找或创建用户
            SysUser sysUser = findOrCreateUser(githubUser);

            // 4. 生成JWT token
            Map<String, Object> claims = new HashMap<>();
            claims.put("userId", sysUser.getId());
            claims.put("email", sysUser.getEmail());
            String token = JwtUtil.generateToken(claims);

            // 5. 存储token到Redis（按userId和按token值两种索引，保持与密码登录一致）
            String tokenKey = RedisConstant.TOKEN_PREFIX + sysUser.getId();
            redisTemplate.opsForValue().set(tokenKey, token, RedisConstant.TOKEN_EXPIRE, TimeUnit.SECONDS);

            String accessTokenValueKey = RedisConstant.TOKEN_PREFIX + token;
            redisTemplate.opsForValue().set(accessTokenValueKey, sysUser.getId(), RedisConstant.TOKEN_EXPIRE, TimeUnit.SECONDS);

            // 6. 构建返回结果
            LoginVO loginVO = new LoginVO();
            loginVO.setToken(token);

            LoginVO.UserInfo userInfo = new LoginVO.UserInfo();
            userInfo.setId(sysUser.getId());
            userInfo.setUsername(sysUser.getUsername());
            userInfo.setEmail(sysUser.getEmail());
            userInfo.setNickname(sysUser.getNickname());

            loginVO.setUserInfo(userInfo);

            return loginVO;

        } catch (Exception e) {
            log.error("GitHub OAuth处理失败", e);
            throw new RuntimeException("GitHub登录失败: " + e.getMessage());
        }
    }

    @Override
    public String getGitHubAuthorizeUrl() {
        String state = UUID.randomUUID().toString();
        return String.format(
            "https://github.com/login/oauth/authorize?client_id=%s&redirect_uri=%s&scope=user:email,read:user&state=%s",
            clientId,
            redirectUri,
            state
        );
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
            sysUser.setStatus(1);
            sysUserMapper.insert(sysUser);

            // 创建用户档案
            UserProfile profile = new UserProfile();
            profile.setId(sysUser.getId());
            profile.setUsername(sysUser.getUsername());
            profile.setDisplayName(githubUser.getName() != null ? githubUser.getName() : githubUser.getLogin());
            profile.setAvatarUrl(githubUser.getAvatarUrl());
            profile.setBio(githubUser.getBio());
            profile.setStatus("offline");
            profile.setLastSeenAt(java.time.LocalDateTime.now());
            profile.setCreateTime(java.time.LocalDateTime.now());
            profile.setUpdateTime(java.time.LocalDateTime.now());
            profile.setDeleted(0);
            userProfileMapper.insert(profile);
        }

        return sysUser;
    }
}
