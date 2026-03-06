package com.nebula.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.nebula.common.constant.AdminConstants;
import com.nebula.common.constant.RedisKey;
import com.nebula.common.exception.BusinessException;
import com.nebula.common.exception.ErrorCode;
import com.nebula.common.util.JwtUtil;
import com.nebula.config.config.JwtProperties;
import com.nebula.config.config.MinioConfig;
import com.nebula.config.properties.GitHubOAuthProperties;
import com.nebula.config.util.MinioUtil;
import com.nebula.model.dto.GitHubOAuthConfirmDTO;
import com.nebula.model.dto.GitHubOAuthDTO;
import com.nebula.model.entity.SysUser;
import com.nebula.model.entity.system.SysUserRole;
import com.nebula.model.vo.GitHubOAuthConfirmVO;
import com.nebula.model.vo.GitHubUserInfo;
import com.nebula.model.vo.LoginVO;
import com.nebula.service.mapper.SysUserMapper;
import com.nebula.service.mapper.system.SysUserRoleMapper;
import com.nebula.service.service.OAuthService;
import com.nebula.service.service.TokenStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
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
import java.util.concurrent.TimeUnit;

/**
 * OAuth服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OAuthServiceImpl implements OAuthService {

    private static final String GITHUB_OAUTH_TEMP_PREFIX = "github:oauth:temp:";
    private static final long TEMP_TOKEN_EXPIRE_SECONDS = 600; // 10分钟过期

    private final SysUserMapper sysUserMapper;
    private final PasswordEncoder passwordEncoder;
    private final TokenStorageService tokenStorageService;
    private final GitHubOAuthProperties gitHubOAuthProperties;
    private final JwtProperties jwtProperties;
    private final RestTemplate restTemplate;
    private final RedisTemplate<String, Object> redisTemplate;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final MinioUtil minioUtil;
    private final MinioConfig minioConfig;
    private final RestTemplate githubRestTemplate;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public GitHubOAuthConfirmVO handleGitHubCallbackForConfirm(GitHubOAuthDTO oauthDTO) {
        log.info("开始处理GitHub OAuth回调, code长度: {}", oauthDTO.getCode() != null ? oauthDTO.getCode().length() : 0);

        try {
            // 1. 用code换取access_token
            String githubAccessToken = exchangeCodeForToken(oauthDTO.getCode());
            log.debug("成功获取GitHub access_token");

            // 2. 获取GitHub用户信息
            GitHubUserInfo githubUser = fetchGitHubUserInfo(githubAccessToken);
            log.info("成功获取GitHub用户信息, GitHub ID: {}, Login: {}", githubUser.getId(), githubUser.getLogin());

            // 3. 检查用户是否已存在
            boolean isNewUser = !isUserExists(githubUser.getId());

            // 4. 生成临时token
            String tempToken = UUID.randomUUID().toString();
            String tempKey = GITHUB_OAUTH_TEMP_PREFIX + tempToken;

            // 5. 暂存GitHub用户信息到Redis
            Map<String, Object> tempData = new HashMap<>();
            tempData.put("githubId", githubUser.getId());
            tempData.put("githubLogin", githubUser.getLogin());
            tempData.put("name", githubUser.getName());
            tempData.put("email", githubUser.getEmail());
            tempData.put("avatarUrl", githubUser.getAvatarUrl());
            tempData.put("bio", githubUser.getBio());
            tempData.put("isNewUser", isNewUser);

            redisTemplate.opsForValue().set(tempKey, tempData, TEMP_TOKEN_EXPIRE_SECONDS, TimeUnit.SECONDS);
            log.info("暂存GitHub用户信息, tempToken: {}, 有效期: {}秒", tempToken, TEMP_TOKEN_EXPIRE_SECONDS);

            // 6. 构建确认信息返回
            String suggestedUsername = "github_" + githubUser.getId();
            String suggestedNickname = githubUser.getName() != null ? githubUser.getName() : githubUser.getLogin();
            String suggestedEmail = githubUser.getEmail() != null ? githubUser.getEmail() : "github_" + githubUser.getId() + "@github.com";

            GitHubOAuthConfirmVO confirmVO = new GitHubOAuthConfirmVO();
            confirmVO.setTempToken(tempToken);
            confirmVO.setGithubId(githubUser.getId());
            confirmVO.setGithubLogin(githubUser.getLogin());
            confirmVO.setUsername(suggestedUsername);
            confirmVO.setNickname(suggestedNickname);
            confirmVO.setEmail(suggestedEmail);
            confirmVO.setAvatarUrl(githubUser.getAvatarUrl());
            confirmVO.setBio(githubUser.getBio());
            confirmVO.setIsNewUser(isNewUser);

            return confirmVO;

        } catch (BusinessException e) {
            log.error("GitHub OAuth业务异常: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("GitHub OAuth处理失败", e);
            throw new BusinessException(ErrorCode.GITHUB_AUTH_FAILED, "GitHub登录失败，请稍后重试");
        }
    }

    @Override
    public GitHubOAuthConfirmVO getGitHubUserInfo(String tempToken) {
        log.info("获取GitHub用户信息, tempToken: {}", tempToken);

        try {
            String tempKey = GITHUB_OAUTH_TEMP_PREFIX + tempToken;
            @SuppressWarnings("unchecked")
            Map<String, Object> tempData = (Map<String, Object>) redisTemplate.opsForValue().get(tempKey);

            if (tempData == null) {
                log.error("GitHub OAuth临时token已过期或不存在: {}", tempToken);
                throw new BusinessException(ErrorCode.GITHUB_AUTH_FAILED, "临时登录凭证已过期，请重新授权");
            }

            Long githubId = ((Number) tempData.get("githubId")).longValue();
            String githubLogin = (String) tempData.get("githubLogin");
            String name = (String) tempData.get("name");
            String originalEmail = (String) tempData.get("email");
            String avatarUrl = (String) tempData.get("avatarUrl");
            String bio = (String) tempData.get("bio");
            boolean isNewUser = (Boolean) tempData.get("isNewUser");

            String suggestedUsername = "github_" + githubId;
            String suggestedNickname = name != null ? name : githubLogin;
            String suggestedEmail = originalEmail != null ? originalEmail : "github_" + githubId + "@github.com";

            GitHubOAuthConfirmVO confirmVO = new GitHubOAuthConfirmVO();
            confirmVO.setTempToken(tempToken);
            confirmVO.setGithubId(githubId);
            confirmVO.setGithubLogin(githubLogin);
            confirmVO.setUsername(suggestedUsername);
            confirmVO.setNickname(suggestedNickname);
            confirmVO.setEmail(suggestedEmail);
            confirmVO.setAvatarUrl(avatarUrl);
            confirmVO.setBio(bio);
            confirmVO.setIsNewUser(isNewUser);

            return confirmVO;

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("获取GitHub用户信息失败", e);
            throw new BusinessException(ErrorCode.GITHUB_AUTH_FAILED, "获取用户信息失败，请重新授权");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LoginVO confirmGitHubLogin(GitHubOAuthConfirmDTO confirmDTO) {
        log.info("开始确认GitHub登录, tempToken: {}", confirmDTO.getTempToken());

        try {
            // 1. 从Redis获取暂存的GitHub用户信息
            String tempKey = GITHUB_OAUTH_TEMP_PREFIX + confirmDTO.getTempToken();
            @SuppressWarnings("unchecked")
            Map<String, Object> tempData = (Map<String, Object>) redisTemplate.opsForValue().get(tempKey);

            if (tempData == null) {
                log.error("GitHub OAuth临时token已过期或不存在: {}", confirmDTO.getTempToken());
                throw new BusinessException(ErrorCode.GITHUB_AUTH_FAILED, "临时登录凭证已过期，请重新授权");
            }

            // 2. 删除临时token，防止重复使用
            redisTemplate.delete(tempKey);

            // 3. 提取GitHub用户信息
            Long githubId = ((Number) tempData.get("githubId")).longValue();
            String githubLogin = (String) tempData.get("githubLogin");
            String name = (String) tempData.get("name");
            String originalEmail = (String) tempData.get("email");
            String avatarUrl = (String) tempData.get("avatarUrl");
            String bio = (String) tempData.get("bio");
            boolean isNewUser = (Boolean) tempData.get("isNewUser");

            // 4. 使用用户确认的信息（如果没有提供，则使用默认值）
            String username = confirmDTO.getUsername() != null ? confirmDTO.getUsername() : "github_" + githubId;
            String nickname = confirmDTO.getNickname() != null ? confirmDTO.getNickname() : (name != null ? name : githubLogin);
            String email = confirmDTO.getEmail() != null ? confirmDTO.getEmail() : (originalEmail != null ? originalEmail : "github_" + githubId + "@github.com");

            // 5. 查找或创建用户
            SysUser sysUser = findOrCreateUser(githubId, username, nickname, email, avatarUrl, bio, isNewUser);
            log.info("用户处理完成, 用户ID: {}, 用户名: {}", sysUser.getId(), sysUser.getUsername());

            // 6. 生成 Token 对
            TokenPair tokens = generateTokens(sysUser);

            // 7. 保存 Token 到 Redis
            long accessTtl = RedisKey.Token.getAccessTokenTtlSeconds(jwtProperties.getAccessTokenExpiration());
            long refreshTtl = RedisKey.Token.getRefreshTokenTtlSeconds(jwtProperties.getRefreshTokenExpiration());
            tokenStorageService.saveTokens(sysUser.getId(), tokens.accessToken(), tokens.refreshToken(), accessTtl, refreshTtl);

            // 8. 更新用户最后登录时间
            updateUserLoginInfo(sysUser);

            // 9. 构建返回结果
            LoginVO loginVO = buildLoginVO(sysUser, tokens.accessToken(), tokens.refreshToken());
            log.info("GitHub OAuth登录成功, 用户ID: {}", sysUser.getId());

            return loginVO;

        } catch (BusinessException e) {
            log.error("GitHub OAuth确认业务异常: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("GitHub OAuth确认失败", e);
            throw new BusinessException(ErrorCode.GITHUB_AUTH_FAILED, "GitHub登录确认失败，请稍后重试");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LoginVO loginGitHubExistingUser(String tempToken) {
        log.info("老用户直接GitHub登录, tempToken: {}", tempToken);

        try {
            // 1. 从Redis获取暂存的GitHub用户信息
            String tempKey = GITHUB_OAUTH_TEMP_PREFIX + tempToken;
            @SuppressWarnings("unchecked")
            Map<String, Object> tempData = (Map<String, Object>) redisTemplate.opsForValue().get(tempKey);

            if (tempData == null) {
                log.error("GitHub OAuth临时token已过期或不存在: {}", tempToken);
                throw new BusinessException(ErrorCode.GITHUB_AUTH_FAILED, "临时登录凭证已过期，请重新授权");
            }

            // 2. 删除临时token，防止重复使用
            redisTemplate.delete(tempKey);

            // 3. 提取GitHub用户信息
            Long githubId = ((Number) tempData.get("githubId")).longValue();
            String githubLogin = (String) tempData.get("githubLogin");
            String avatarUrl = (String) tempData.get("avatarUrl");
            boolean isNewUser = (Boolean) tempData.get("isNewUser");

            if (isNewUser) {
                log.error("新用户不能使用老用户直接登录接口, githubId: {}", githubId);
                throw new BusinessException(ErrorCode.GITHUB_AUTH_FAILED, "新用户需要先确认信息");
            }

            // 4. 查找现有用户
            LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysUser::getUsername, "github_" + githubId);
            SysUser sysUser = sysUserMapper.selectOne(wrapper);

            if (sysUser == null) {
                log.error("未找到GitHub用户, githubId: {}", githubId);
                throw new BusinessException(ErrorCode.USER_NOT_FOUND);
            }

            // 5. 更新用户信息（包括头像）
            boolean needUpdate = false;
            if (avatarUrl != null) {
                try {
                    Map<String, Object> uploadResult = uploadAvatarToMinio(avatarUrl, githubId);
                    sysUser.setAvatarName((String) uploadResult.get("fileName"));
                    sysUser.setAvatarSize((Long) uploadResult.get("fileSize"));
                    sysUser.setAvatarUrl((String) uploadResult.get("fileUrl"));
                    needUpdate = true;
                    log.info("更新GitHub用户头像成功, 用户ID: {}", sysUser.getId());
                } catch (Exception e) {
                    log.warn("上传GitHub头像到MinIO失败，使用GitHub原始URL: {}", e.getMessage());
                    sysUser.setAvatarUrl(avatarUrl);
                    needUpdate = true;
                }
            }
            if (needUpdate) {
                sysUserMapper.updateById(sysUser);
            }

            // 6. 生成 Token 对
            TokenPair tokens = generateTokens(sysUser);

            // 7. 保存 Token 到 Redis
            long accessTtl = RedisKey.Token.getAccessTokenTtlSeconds(jwtProperties.getAccessTokenExpiration());
            long refreshTtl = RedisKey.Token.getRefreshTokenTtlSeconds(jwtProperties.getRefreshTokenExpiration());
            tokenStorageService.saveTokens(sysUser.getId(), tokens.accessToken(), tokens.refreshToken(), accessTtl, refreshTtl);

            // 8. 更新用户最后登录时间
            updateUserLoginInfo(sysUser);

            // 9. 构建返回结果
            LoginVO loginVO = buildLoginVO(sysUser, tokens.accessToken(), tokens.refreshToken());
            log.info("GitHub老用户直接登录成功, 用户ID: {}", sysUser.getId());

            return loginVO;

        } catch (BusinessException e) {
            log.error("GitHub老用户登录业务异常: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("GitHub老用户登录失败", e);
            throw new BusinessException(ErrorCode.GITHUB_AUTH_FAILED, "GitHub登录失败，请稍后重试");
        }
    }

    /**
     * 检查用户是否已存在
     */
    private boolean isUserExists(Long githubId) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, "github_" + githubId);
        return sysUserMapper.selectCount(wrapper) > 0;
    }

    /**
     * 查找或创建用户
     */
    private SysUser findOrCreateUser(Long githubId, String username, String nickname, String email,
                                      String githubAvatarUrl, String bio, boolean isNewUser) {
        if (!isNewUser) {
            // 查找现有用户
            LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysUser::getUsername, "github_" + githubId);
            SysUser sysUser = sysUserMapper.selectOne(wrapper);
            if (sysUser != null) {
                // 更新用户信息（可选更新）
                boolean needUpdate = false;
                if (nickname != null && !nickname.equals(sysUser.getNickname())) {
                    sysUser.setNickname(nickname);
                    needUpdate = true;
                }
                if (bio != null && !bio.equals(sysUser.getBio())) {
                    sysUser.setBio(bio);
                    needUpdate = true;
                }
                // 每次登录都更新头像（GitHub用户可能更换头像）
                if (githubAvatarUrl != null) {
                    try {
                        Map<String, Object> uploadResult = uploadAvatarToMinio(githubAvatarUrl, githubId);
                        sysUser.setAvatarName((String) uploadResult.get("fileName"));
                        sysUser.setAvatarSize((Long) uploadResult.get("fileSize"));
                        sysUser.setAvatarUrl((String) uploadResult.get("fileUrl"));
                        needUpdate = true;
                        log.info("更新GitHub用户头像成功, 用户ID: {}", sysUser.getId());
                    } catch (Exception e) {
                        log.warn("上传GitHub头像到MinIO失败，使用GitHub原始URL: {}", e.getMessage());
                        sysUser.setAvatarUrl(githubAvatarUrl);
                        needUpdate = true;
                    }
                }
                if (needUpdate) {
                    sysUserMapper.updateById(sysUser);
                }
                return sysUser;
            }
        }

        // 创建新用户
        SysUser sysUser = new SysUser();
        sysUser.setUsername(username);
        sysUser.setPassword(passwordEncoder.encode(UUID.randomUUID().toString())); // 随机密码
        sysUser.setEmail(email);
        sysUser.setNickname(nickname);
        sysUser.setBio(bio);
        sysUser.setOnlineStatus("offline");
        sysUser.setLastSeenAt(OffsetDateTime.now());

        // 上传GitHub头像到MinIO
        if (githubAvatarUrl != null) {
            try {
                Map<String, Object> uploadResult = uploadAvatarToMinio(githubAvatarUrl, githubId);
                sysUser.setAvatarName((String) uploadResult.get("fileName"));
                sysUser.setAvatarSize((Long) uploadResult.get("fileSize"));
                sysUser.setAvatarUrl((String) uploadResult.get("fileUrl"));
            } catch (Exception e) {
                log.warn("上传GitHub头像到MinIO失败，使用GitHub原始URL: {}", e.getMessage());
                sysUser.setAvatarUrl(githubAvatarUrl);
            }
        }

        sysUserMapper.insert(sysUser);

        // 给新用户分配默认普通用户角色
        if (isNewUser) {
            assignDefaultRoleToUser(sysUser.getId());
        }

        return sysUser;
    }

    /**
     * 上传GitHub头像到MinIO
     *
     * @param githubAvatarUrl GitHub头像URL
     * @param githubId GitHub用户ID
     * @return 包含fileName、fileSize、fileUrl的Map
     */
    private Map<String, Object> uploadAvatarToMinio(String githubAvatarUrl, Long githubId) {
        log.info("开始上传GitHub头像到MinIO, GitHub ID: {}, URL: {}", githubId, githubAvatarUrl);

        String fileName = "avatars/github_" + githubId + "_" + System.currentTimeMillis() + ".jpg";
        Map<String, Object> result = minioUtil.uploadImageFromUrl(
                minioConfig.getBucketName(),
                githubAvatarUrl,
                fileName
        );

        log.info("GitHub头像上传成功, GitHub ID: {}, 结果: {}", githubId, result);
        return result;
    }

    /**
     * 给用户分配默认普通用户角色
     */
    private void assignDefaultRoleToUser(Long userId) {
        SysUserRole userRole = new SysUserRole();
        userRole.setUserId(userId);
        userRole.setRoleId(AdminConstants.DEFAULT_USER_ROLE_ID);
        sysUserRoleMapper.insert(userRole);
        log.info("给GitHub新用户分配默认角色, 用户ID: {}, 角色ID: {}", userId, AdminConstants.DEFAULT_USER_ROLE_ID);
    }

    /**
     * 用code换取access_token
     */
    private String exchangeCodeForToken(String code) {
        try {
            log.info("开始换取GitHub access_token, code长度: {}", code != null ? code.length() : 0);
            log.info("GitHub OAuth配置 - clientId: {}, redirectUri: {}",
                    gitHubOAuthProperties.getClientId(),
                    gitHubOAuthProperties.getRedirectUri());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.set("Accept", "application/json");

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("client_id", gitHubOAuthProperties.getClientId());
            params.add("client_secret", gitHubOAuthProperties.getClientSecret());
            params.add("code", code);
            params.add("redirect_uri", gitHubOAuthProperties.getRedirectUri());

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

            log.info("请求GitHub token API: {}", gitHubOAuthProperties.getTokenUrl());

            // 先以字符串形式获取响应，便于调试
            ResponseEntity<String> stringResponse = restTemplate.exchange(
                    gitHubOAuthProperties.getTokenUrl(),
                    HttpMethod.POST,
                    request,
                    String.class
            );

            log.info("GitHub token API响应状态: {}", stringResponse.getStatusCode());
            log.info("GitHub token API原始响应: {}", stringResponse.getBody());

            // 手动解析响应
            String responseBody = stringResponse.getBody();
            if (responseBody == null || responseBody.isEmpty()) {
                log.error("GitHub返回的响应为空");
                throw new BusinessException(ErrorCode.GITHUB_AUTH_FAILED, "获取GitHub访问令牌失败");
            }

            String accessToken = null;

            // 尝试解析JSON格式
            if (responseBody.trim().startsWith("{")) {
                log.info("检测到JSON格式响应");
                try {
                    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                    java.util.Map<String, Object> jsonMap = mapper.readValue(responseBody, java.util.Map.class);
                    accessToken = (String) jsonMap.get("access_token");
                } catch (Exception e) {
                    log.warn("解析JSON失败: {}", e.getMessage());
                }
            }

            // 如果JSON没解析到，尝试表单格式
            if (accessToken == null) {
                log.info("尝试解析表单格式响应");
                String[] pairs = responseBody.split("&");
                for (String pair : pairs) {
                    String[] keyValue = pair.split("=", 2);
                    if (keyValue.length == 2 && "access_token".equals(keyValue[0])) {
                        accessToken = keyValue[1];
                        break;
                    }
                }
            }

            if (accessToken == null) {
                log.error("GitHub响应中未找到access_token. 响应内容: {}", responseBody);
                throw new BusinessException(ErrorCode.GITHUB_AUTH_FAILED, "获取GitHub访问令牌失败，响应中未找到access_token");
            }

            log.info("成功解析GitHub access_token, 长度: {}", accessToken.length());
            return accessToken;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("换取GitHub access_token失败", e);
            throw new BusinessException(ErrorCode.GITHUB_AUTH_FAILED, "获取GitHub访问令牌失败: " + e.getMessage());
        }
    }

    /**
     * 获取GitHub用户信息
     */
    private GitHubUserInfo fetchGitHubUserInfo(String accessToken) {
        try {
            log.info("开始获取GitHub用户信息, accessToken长度: {}", accessToken != null ? accessToken.length() : 0);

            HttpHeaders headers = new HttpHeaders();
            if (accessToken != null) {
                headers.setBearerAuth(accessToken);
            }
            headers.set("Accept", "application/vnd.github.v3+json");

            HttpEntity<Void> request = new HttpEntity<>(headers);

            log.info("请求GitHub用户信息API: {}", gitHubOAuthProperties.getUserInfoUrl());

            ResponseEntity<String> stringResponse = restTemplate.exchange(
                    gitHubOAuthProperties.getUserInfoUrl(),
                    HttpMethod.GET,
                    request,
                    String.class
            );

            String responseBody = stringResponse.getBody();
            log.info("GitHub返回原始响应: {}", responseBody);

            if (responseBody == null || responseBody.isEmpty()) {
                log.error("GitHub返回的用户信息为空");
                throw new BusinessException(ErrorCode.OAUTH_USER_INFO_ERROR, "获取GitHub用户信息失败");
            }

            // 手动解析JSON
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            java.util.Map<String, Object> jsonMap = mapper.readValue(responseBody, java.util.Map.class);

            GitHubUserInfo userInfo = new GitHubUserInfo();
            userInfo.setId(((Number) jsonMap.get("id")).longValue());
            userInfo.setLogin((String) jsonMap.get("login"));
            userInfo.setName((String) jsonMap.get("name"));
            userInfo.setEmail((String) jsonMap.get("email"));
            userInfo.setAvatarUrl((String) jsonMap.get("avatar_url"));
            userInfo.setBio((String) jsonMap.get("bio"));

            log.info("成功解析GitHub用户信息: ID={}, Login={}, AvatarUrl={}",
                    userInfo.getId(), userInfo.getLogin(), userInfo.getAvatarUrl());

            return userInfo;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("获取GitHub用户信息失败", e);
            throw new BusinessException(ErrorCode.OAUTH_USER_INFO_ERROR, "获取GitHub用户信息失败: " + e.getMessage());
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
        userInfo.setAvatarName(sysUser.getAvatarName());
        userInfo.setAvatarSize(sysUser.getAvatarSize());
        userInfo.setAvatarUrl(sysUser.getAvatarUrl());
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
}
