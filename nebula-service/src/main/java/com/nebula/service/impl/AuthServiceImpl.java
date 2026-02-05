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

        // 存储Access Token到Redis
        String tokenKey = RedisConstant.TOKEN_PREFIX + sysUser.getId();
        redisTemplate.opsForValue().set(
                tokenKey,
                accessToken,
                jwtProperties.getAccessTokenExpiration() / 1000,
                TimeUnit.SECONDS
        );

        // 存储Refresh Token到Redis（key不同，用refresh前缀）
        String refreshTokenKey = RedisConstant.TOKEN_PREFIX + "refresh:" + sysUser.getId();
        redisTemplate.opsForValue().set(
                refreshTokenKey,
                refreshToken,
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
        userInfo.setAvatar(profile != null ? profile.getAvatarUrl() : null);

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

        // 存储Refresh Token到Redis
        String refreshTokenKey = RedisConstant.TOKEN_PREFIX + "refresh:" + sysUser.getId();
        redisTemplate.opsForValue().set(
                refreshTokenKey,
                refreshToken,
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
        userInfo.setAvatar(null);

        loginVO.setUserInfo(userInfo);

        log.info("用户注册成功: userId={}, email={}", sysUser.getId(), sysUser.getEmail());
        return loginVO;
    }

    @Override
    public void logout(String token) {
        try {
            // 从token中获取用户ID（使用配置的密钥）
            Long userId = JwtUtil.parseToken(token, jwtProperties.getSecret()).get("userId", Long.class);
            if (userId != null) {
                // 删除Redis中的access token
                String tokenKey = RedisConstant.TOKEN_PREFIX + userId;
                redisTemplate.delete(tokenKey);

                // 删除Redis中的refresh token
                String refreshTokenKey = RedisConstant.TOKEN_PREFIX + "refresh:" + userId;
                redisTemplate.delete(refreshTokenKey);

                log.info("用户登出成功: userId={}", userId);
            }
        } catch (Exception e) {
            log.error("登出失败", e);
        }
    }

    @Override
    public LoginVO.UserInfo getUserInfo(String token) {
        try {
            // 验证token并获取用户ID（使用配置的密钥）
            // JwtUtil.parseToken() 已经验证了JWT签名和过期时间
            Long userId = JwtUtil.parseToken(token, jwtProperties.getSecret()).get("userId", Long.class);
            if (userId == null) {
                throw new RuntimeException("无效的token");
            }

            // 检查Redis中是否存在该用户的token（会话检查）
            // 不要求token完全匹配，只要用户有有效会话即可
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
            userInfo.setAvatar(profile != null ? profile.getAvatarUrl() : null);

            return userInfo;
        } catch (Exception e) {
            log.error("获取用户信息失败", e);
            throw new RuntimeException("获取用户信息失败: " + e.getMessage());
        }
    }

    @Override
    public Long validateToken(String token) {
        try {
            // 移除 "Bearer " 前缀
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            // 验证token并获取用户ID（使用配置的密钥）
            // JwtUtil.parseToken() 已经验证了JWT签名和过期时间
            Long userId = JwtUtil.parseToken(token, jwtProperties.getSecret()).get("userId", Long.class);
            if (userId == null) {
                throw new RuntimeException("无效的token");
            }

            // 检查Redis中是否存在该用户的token（会话检查）
            // 不要求token完全匹配，只要用户有有效会话即可
            String tokenKey = RedisConstant.TOKEN_PREFIX + userId;
            String cachedToken = (String) redisTemplate.opsForValue().get(tokenKey);
            if (cachedToken == null) {
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
            // 解析refresh token（使用配置的密钥）
            Long userId = JwtUtil.parseToken(refreshToken, jwtProperties.getSecret()).get("userId", Long.class);
            if (userId == null) {
                throw new RuntimeException("无效的refresh token");
            }

            // 验证refresh token是否在Redis中
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

            redisTemplate.opsForValue().set(
                    refreshTokenKey,
                    newRefreshToken,
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
            userInfo.setAvatar(profile != null ? profile.getAvatarUrl() : null);

            loginVO.setUserInfo(userInfo);

            log.info("刷新token成功: userId={}", userId);
            return loginVO;
        } catch (Exception e) {
            log.error("刷新token失败", e);
            throw new RuntimeException("刷新token失败: " + e.getMessage());
        }
    }
}
