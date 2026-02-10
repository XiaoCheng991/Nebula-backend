package com.nebula.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.nebula.common.util.JwtUtil;
import com.nebula.config.config.JwtProperties;
import com.nebula.config.constant.RedisConstant;
import com.nebula.model.dto.LoginDTO;
import com.nebula.model.dto.RegisterDTO;
import com.nebula.model.entity.SysUser;
import com.nebula.model.entity.UserProfile;
import com.nebula.model.vo.LoginVO;
import com.nebula.service.mapper.SysUserMapper;
import com.nebula.service.mapper.UserProfileMapper;
import com.nebula.service.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 认证服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final SysUserMapper sysUserMapper;
    private final UserProfileMapper userProfileMapper;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String, Object> redisTemplate;
    private final JwtProperties jwtProperties;

    @Override
    @Transactional
    public LoginVO login(LoginDTO loginDTO) {
        // 查找用户（通过邮箱）
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getEmail, loginDTO.getEmail());
        SysUser sysUser = sysUserMapper.selectOne(wrapper);

        if (sysUser == null) {
            throw new RuntimeException("用户不存在");
        }

        // 验证密码
        if (!passwordEncoder.matches(loginDTO.getPassword(), sysUser.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        // 检查用户状态
        if (sysUser.getStatus() == 0) {
            throw new RuntimeException("账号已被禁用");
        }

        // 查找用户档案
        UserProfile profile = userProfileMapper.selectById(sysUser.getId());

        // 生成Access Token和Refresh Token
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

        // 存储Access Token到Redis（按userId和按token值两种索引，方便根据token或userId查找）
        String tokenKey = RedisConstant.TOKEN_PREFIX + sysUser.getId();
        redisTemplate.opsForValue().set(
                tokenKey,
                accessToken,
                jwtProperties.getAccessTokenExpiration() / 1000,
                TimeUnit.SECONDS
        );
        String accessTokenValueKey = RedisConstant.TOKEN_PREFIX + accessToken;
        redisTemplate.opsForValue().set(
                accessTokenValueKey,
                sysUser.getId(),
                jwtProperties.getAccessTokenExpiration() / 1000,
                TimeUnit.SECONDS
        );

        // 存储Refresh Token到Redis（按userId和按token值两种索引）
        String refreshTokenKey = RedisConstant.TOKEN_PREFIX + "refresh:" + sysUser.getId();
        redisTemplate.opsForValue().set(
                refreshTokenKey,
                refreshToken,
                jwtProperties.getRefreshTokenExpiration() / 1000,
                TimeUnit.SECONDS
        );
        String refreshTokenValueKey = RedisConstant.TOKEN_PREFIX + "refresh_token_value:" + refreshToken;
        redisTemplate.opsForValue().set(
                refreshTokenValueKey,
                sysUser.getId(),
                jwtProperties.getRefreshTokenExpiration() / 1000,
                TimeUnit.SECONDS
        );

        // 更新最后登录时间
        sysUser.setLastLoginAt(java.time.LocalDateTime.now());
        sysUserMapper.updateById(sysUser);

        // 构建返回结果
        LoginVO loginVO = new LoginVO();
        loginVO.setToken(accessToken);
        loginVO.setRefreshToken(refreshToken);

        LoginVO.UserInfo userInfo = new LoginVO.UserInfo();
        userInfo.setId(sysUser.getId());
        userInfo.setUsername(sysUser.getUsername());
        userInfo.setEmail(sysUser.getEmail());
        userInfo.setNickname(sysUser.getNickname());

        loginVO.setUserInfo(userInfo);

        log.info("用户登录成功: userId={}, email={}", sysUser.getId(), sysUser.getEmail());
        return loginVO;
    }

    @Override
    @Transactional
    public LoginVO register(RegisterDTO registerDTO) {
        // 检查邮箱是否已存在
        LambdaQueryWrapper<SysUser> emailWrapper = new LambdaQueryWrapper<>();
        emailWrapper.eq(SysUser::getEmail, registerDTO.getEmail());
        if (sysUserMapper.selectCount(emailWrapper) > 0) {
            throw new RuntimeException("邮箱已被注册");
        }

        // 检查用户名是否已存在
        LambdaQueryWrapper<SysUser> usernameWrapper = new LambdaQueryWrapper<>();
        usernameWrapper.eq(SysUser::getUsername, registerDTO.getUsername());
        if (sysUserMapper.selectCount(usernameWrapper) > 0) {
            throw new RuntimeException("用户名已被占用");
        }

        // 创建系统用户
        SysUser sysUser = new SysUser();
        sysUser.setUsername(registerDTO.getUsername());
        sysUser.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        sysUser.setEmail(registerDTO.getEmail());
        sysUser.setNickname(registerDTO.getNickname() != null ? registerDTO.getNickname() : registerDTO.getUsername());
        sysUser.setStatus(1);
        sysUserMapper.insert(sysUser);

        // 创建用户档案
        UserProfile profile = new UserProfile();
        profile.setId(sysUser.getId());
        profile.setUsername(registerDTO.getUsername());
        profile.setDisplayName(registerDTO.getNickname());
        profile.setStatus("offline");
        profile.setLastSeenAt(java.time.LocalDateTime.now());
        profile.setCreateTime(java.time.LocalDateTime.now());
        profile.setUpdateTime(java.time.LocalDateTime.now());
        profile.setDeleted(0);
        userProfileMapper.insert(profile);

        // 自动登录 - 生成Access Token和Refresh Token
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

        // 存储Access Token到Redis
        String tokenKey = RedisConstant.TOKEN_PREFIX + sysUser.getId();
        redisTemplate.opsForValue().set(
                tokenKey,
                accessToken,
                jwtProperties.getAccessTokenExpiration() / 1000,
                TimeUnit.SECONDS
        );
        String accessTokenValueKey = RedisConstant.TOKEN_PREFIX + accessToken;
        redisTemplate.opsForValue().set(
                accessTokenValueKey,
                sysUser.getId(),
                jwtProperties.getAccessTokenExpiration() / 1000,
                TimeUnit.SECONDS
        );

        // 存储Refresh Token到Redis
        String refreshTokenKey = RedisConstant.TOKEN_PREFIX + "refresh:" + sysUser.getId();
        redisTemplate.opsForValue().set(
                refreshTokenKey,
                refreshToken,
                jwtProperties.getRefreshTokenExpiration() / 1000,
                TimeUnit.SECONDS
        );
        String refreshTokenValueKey = RedisConstant.TOKEN_PREFIX + "refresh_token_value:" + refreshToken;
        redisTemplate.opsForValue().set(
                refreshTokenValueKey,
                sysUser.getId(),
                jwtProperties.getRefreshTokenExpiration() / 1000,
                TimeUnit.SECONDS
        );

        // 构建返回结果
        LoginVO loginVO = new LoginVO();
        loginVO.setToken(accessToken);
        loginVO.setRefreshToken(refreshToken);

        LoginVO.UserInfo userInfo = new LoginVO.UserInfo();
        userInfo.setId(sysUser.getId());
        userInfo.setUsername(sysUser.getUsername());
        userInfo.setEmail(sysUser.getEmail());
        userInfo.setNickname(sysUser.getNickname());

        loginVO.setUserInfo(userInfo);

        log.info("用户注册成功: userId={}, email={}", sysUser.getId(), sysUser.getEmail());
        return loginVO;
    }

    @Override
    public void logout(Long userId) {
        try {
            if (userId == null) {
                throw new RuntimeException("用户未登录");
            }

            // 删除Redis中的access token（按userId和按token值两种索引）
            String tokenKey = RedisConstant.TOKEN_PREFIX + userId;
            String accessToken = (String) redisTemplate.opsForValue().get(tokenKey);
            if (accessToken != null) {
                String accessTokenValueKey = RedisConstant.TOKEN_PREFIX + accessToken;
                redisTemplate.delete(accessTokenValueKey);
            }
            redisTemplate.delete(tokenKey);

            // 删除Redis中的refresh token（按userId和按token值两种索引）
            String refreshTokenKey = RedisConstant.TOKEN_PREFIX + "refresh:" + userId;
            String refreshToken = (String) redisTemplate.opsForValue().get(refreshTokenKey);
            if (refreshToken != null) {
                String refreshTokenValueKey = RedisConstant.TOKEN_PREFIX + "refresh_token_value:" + refreshToken;
                redisTemplate.delete(refreshTokenValueKey);
            }
            redisTemplate.delete(refreshTokenKey);

            log.info("用户登出成功: userId={}", userId);
        } catch (Exception e) {
            log.error("登出失败", e);
        }
    }

    @Override
    public LoginVO.UserInfo getUserInfo(Long userId) {
        try {
            if (userId == null) {
                throw new RuntimeException("用户未登录");
            }

            // 会话检查：确认该用户仍有有效的 access token
            String tokenKey = RedisConstant.TOKEN_PREFIX + userId;
            String cachedToken = (String) redisTemplate.opsForValue().get(tokenKey);
            if (cachedToken == null) {
                throw new RuntimeException("token已过期或用户未登录");
            }

            // 查询用户信息
            SysUser sysUser = sysUserMapper.selectById(userId);
            if (sysUser == null || sysUser.getDeleted() == 1) {
                throw new RuntimeException("用户不存在");
            }

            UserProfile profile = userProfileMapper.selectById(userId);

            // 构建返回结果
            LoginVO.UserInfo userInfo = new LoginVO.UserInfo();
            userInfo.setId(sysUser.getId());
            userInfo.setUsername(sysUser.getUsername());
            userInfo.setEmail(sysUser.getEmail());
            userInfo.setNickname(sysUser.getNickname());

            return userInfo;
        } catch (Exception e) {
            log.error("获取用户信息失败", e);
            throw new RuntimeException("获取用户信息失败: " + e.getMessage());
        }
    }

    @Override
    public Long validateToken(String token) {
        try {
            if (token == null || token.trim().isEmpty()) {
                throw new RuntimeException("token不能为空");
            }

            // 清理token字符串（移除前后空格和Bearer前缀）
            token = token.trim();
            if (token.startsWith("Bearer ")) {
                token = token.substring(7).trim();
            }

            // 根据 token 值从 Redis 中获取 userId（不再解析JWT）
            String accessTokenValueKey = RedisConstant.TOKEN_PREFIX + token;
            Long userId = Long.valueOf(Objects.requireNonNull(redisTemplate.opsForValue().get(accessTokenValueKey)).toString());

            // 再次确认该用户会话仍然存在
            String tokenKey = RedisConstant.TOKEN_PREFIX + userId;
            String cachedToken = (String) redisTemplate.opsForValue().get(tokenKey);
            if (cachedToken == null || !cachedToken.equals(token)) {
                throw new RuntimeException("token已过期或用户未登录");
            }

            return userId;
        } catch (Exception e) {
            log.error("验证token失败", e);
            throw new RuntimeException("验证token失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public LoginVO refreshToken(String refreshToken) {
        try {
            if (refreshToken == null || refreshToken.trim().isEmpty()) {
                throw new RuntimeException("refresh token不能为空");
            }

            refreshToken = refreshToken.trim();

            // 根据 refresh token 值从 Redis 中获取 userId（不再解析JWT）
            String refreshTokenValueKey = RedisConstant.TOKEN_PREFIX + "refresh_token_value:" + refreshToken;
            Long userId = (Long) redisTemplate.opsForValue().get(refreshTokenValueKey);
            if (userId == null) {
                throw new RuntimeException("refresh token已过期或无效");
            }

            // 再从按 userId 的 key 再次确认 refresh token 是否一致
            String refreshTokenKey = RedisConstant.TOKEN_PREFIX + "refresh:" + userId;
            String cachedRefreshToken = (String) redisTemplate.opsForValue().get(refreshTokenKey);
            if (cachedRefreshToken == null || !cachedRefreshToken.equals(refreshToken)) {
                throw new RuntimeException("refresh token已过期或无效");
            }

            // 查询用户信息
            SysUser sysUser = sysUserMapper.selectById(userId);
            if (sysUser == null || sysUser.getDeleted() == 1 || sysUser.getStatus() == 0) {
                throw new RuntimeException("用户不存在或已被禁用");
            }

            UserProfile profile = userProfileMapper.selectById(userId);

            // 生成新的Access Token和Refresh Token
            Map<String, Object> claims = new HashMap<>();
            claims.put("userId", sysUser.getId());
            claims.put("email", sysUser.getEmail());
            claims.put("type", "access");

            String newAccessToken = JwtUtil.generateAccessToken(
                    claims,
                    jwtProperties.getSecret(),
                    jwtProperties.getAccessTokenExpiration()
            );

            claims.put("type", "refresh");
            String newRefreshToken = JwtUtil.generateRefreshToken(
                    claims,
                    jwtProperties.getSecret(),
                    jwtProperties.getRefreshTokenExpiration()
            );

            // 更新Redis中的token
            String tokenKey = RedisConstant.TOKEN_PREFIX + sysUser.getId();
            redisTemplate.opsForValue().set(
                    tokenKey,
                    newAccessToken,
                    jwtProperties.getAccessTokenExpiration() / 1000,
                    TimeUnit.SECONDS
            );
            String newAccessTokenValueKey = RedisConstant.TOKEN_PREFIX + newAccessToken;
            redisTemplate.opsForValue().set(
                    newAccessTokenValueKey,
                    sysUser.getId(),
                    jwtProperties.getAccessTokenExpiration() / 1000,
                    TimeUnit.SECONDS
            );

            redisTemplate.opsForValue().set(
                    refreshTokenKey,
                    newRefreshToken,
                    jwtProperties.getRefreshTokenExpiration() / 1000,
                    TimeUnit.SECONDS
            );
            String newRefreshTokenValueKey = RedisConstant.TOKEN_PREFIX + "refresh_token_value:" + newRefreshToken;
            redisTemplate.opsForValue().set(
                    newRefreshTokenValueKey,
                    sysUser.getId(),
                    jwtProperties.getRefreshTokenExpiration() / 1000,
                    TimeUnit.SECONDS
            );

            // 构建返回结果
            LoginVO loginVO = new LoginVO();
            loginVO.setToken(newAccessToken);
            loginVO.setRefreshToken(newRefreshToken);

            LoginVO.UserInfo userInfo = new LoginVO.UserInfo();
            userInfo.setId(sysUser.getId());
            userInfo.setUsername(sysUser.getUsername());
            userInfo.setEmail(sysUser.getEmail());
            userInfo.setNickname(sysUser.getNickname());

            loginVO.setUserInfo(userInfo);

            log.info("刷新token成功: userId={}", userId);
            return loginVO;
        } catch (Exception e) {
            log.error("刷新token失败", e);
            throw new RuntimeException("刷新token失败: " + e.getMessage());
        }
    }
}
