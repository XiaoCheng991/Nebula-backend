package com.nebula.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.nebula.common.constant.RedisKey;
import com.nebula.common.exception.BusinessException;
import com.nebula.common.exception.ErrorCode;
import com.nebula.common.util.JwtUtil;
import com.nebula.common.util.LogUtil;
import com.nebula.config.config.JwtProperties;
import com.nebula.model.dto.LoginDTO;
import com.nebula.model.dto.RegisterDTO;
import com.nebula.model.entity.SysUser;
import com.nebula.model.entity.UserProfile;
import com.nebula.model.vo.LoginVO;
import com.nebula.service.mapper.SysUserMapper;
import com.nebula.service.mapper.UserProfileMapper;
import com.nebula.service.service.AuthService;
import com.nebula.service.service.TokenStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

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
    private final TokenStorageService tokenStorageService;
    private final JwtProperties jwtProperties;

    @Override
    @Transactional
    public LoginVO login(LoginDTO loginDTO) {
        // 查找用户（通过邮箱）
        SysUser sysUser = findUserByEmail(loginDTO.getEmail());

        // 验证密码
        if (!passwordEncoder.matches(loginDTO.getPassword(), sysUser.getPassword())) {
            LogUtil.Auth.loginFailed(log, loginDTO.getEmail(), "密码错误");
            throw new BusinessException(ErrorCode.PASSWORD_ERROR);
        }

        // 检查用户状态
        if (sysUser.getStatus() == 0) {
            LogUtil.Auth.loginFailed(log, loginDTO.getEmail(), "账号已被禁用");
            throw new BusinessException(ErrorCode.USER_DISABLED);
        }

        // 生成 Token
        TokenPair tokens = generateTokens(sysUser);

        // 保存 Token 到 Redis
        long accessTtl = RedisKey.Token.getAccessTokenTtlSeconds(jwtProperties.getAccessTokenExpiration());
        long refreshTtl = RedisKey.Token.getRefreshTokenTtlSeconds(jwtProperties.getRefreshTokenExpiration());
        tokenStorageService.saveTokens(sysUser.getId(), tokens.accessToken(), tokens.refreshToken(), accessTtl, refreshTtl);

        // 更新最后登录时间
        sysUser.setLastLoginAt(LocalDateTime.now());
        sysUserMapper.updateById(sysUser);
        LogUtil.Database.update(log, "sys_user", sysUser.getId());

        // 构建返回结果
        LoginVO loginVO = buildLoginVO(sysUser, tokens.accessToken(), tokens.refreshToken());

        LogUtil.Auth.loginSuccess(log, sysUser.getId(), sysUser.getEmail());
        return loginVO;
    }

    @Override
    @Transactional
    public LoginVO register(RegisterDTO registerDTO) {
        // 检查邮箱是否已存在
        if (isEmailExists(registerDTO.getEmail())) {
            LogUtil.Auth.registerFailed(log, registerDTO.getEmail(), "邮箱已被注册");
            throw new BusinessException(ErrorCode.EMAIL_EXISTS);
        }

        // 检查用户名是否已存在
        if (isUsernameExists(registerDTO.getUsername())) {
            LogUtil.Auth.registerFailed(log, registerDTO.getEmail(), "用户名已被占用");
            throw new BusinessException(ErrorCode.USERNAME_EXISTS);
        }

        // 创建系统用户
        SysUser sysUser = createSysUser(registerDTO);
        sysUserMapper.insert(sysUser);
        LogUtil.Database.insert(log, "sys_user", sysUser.getId());

        // 创建用户档案
        UserProfile profile = createUserProfile(sysUser);
        userProfileMapper.insert(profile);
        LogUtil.Database.insert(log, "user_profile", sysUser.getId());

        // 生成 Token
        TokenPair tokens = generateTokens(sysUser);

        // 保存 Token 到 Redis
        long accessTtl = RedisKey.Token.getAccessTokenTtlSeconds(jwtProperties.getAccessTokenExpiration());
        long refreshTtl = RedisKey.Token.getRefreshTokenTtlSeconds(jwtProperties.getRefreshTokenExpiration());
        tokenStorageService.saveTokens(sysUser.getId(), tokens.accessToken(), tokens.refreshToken(), accessTtl, refreshTtl);

        // 构建返回结果
        LoginVO loginVO = buildLoginVO(sysUser, tokens.accessToken(), tokens.refreshToken());

        LogUtil.Auth.registerSuccess(log, sysUser.getId(), sysUser.getEmail(), sysUser.getUsername());
        return loginVO;
    }

    @Override
    public void logout(Long userId) {
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        tokenStorageService.deleteTokens(userId);
        LogUtil.Auth.logout(log, userId);
    }

    @Override
    public LoginVO.UserInfo getUserInfo(Long userId) {
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        // 检查 Token 是否有效
        String currentToken = tokenStorageService.getAccessToken(userId);
        if (currentToken == null) {
            throw new BusinessException(ErrorCode.TOKEN_EXPIRED);
        }

        // 查询用户信息
        SysUser sysUser = sysUserMapper.selectById(userId);
        if (sysUser == null || sysUser.getDeleted() == 1) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        userProfileMapper.selectById(userId);
        LogUtil.Auth.getUserInfo(log, userId);

        return buildUserInfo(sysUser);
    }

    @Override
    public Long validateToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            throw new BusinessException(ErrorCode.TOKEN_MISSING);
        }

        // 清理 token 字符串
        token = token.trim();
        if (token.startsWith("Bearer ")) {
            token = token.substring(7).trim();
        }

        // 验证 Token 是否有效
        if (!tokenStorageService.validateAccessToken(token)) {
            LogUtil.Auth.tokenValidateFailed(log, token, "Token已过期或无效");
            throw new BusinessException(ErrorCode.TOKEN_EXPIRED);
        }

        return tokenStorageService.getUserIdByAccessToken(token);
    }

    @Override
    @Transactional
    public LoginVO refreshToken(String refreshToken) {
        if (refreshToken == null || refreshToken.trim().isEmpty()) {
            throw new BusinessException(ErrorCode.REFRESH_TOKEN_INVALID);
        }

        refreshToken = refreshToken.trim();

        // 验证 Refresh Token
        Long userId = tokenStorageService.getUserIdByRefreshToken(refreshToken);
        if (userId == null) {
            LogUtil.Auth.tokenRefreshFailed(log, "Refresh Token已过期或无效");
            throw new BusinessException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        }

        // 查询用户信息
        SysUser sysUser = sysUserMapper.selectById(userId);
        if (sysUser == null || sysUser.getDeleted() == 1 || sysUser.getStatus() == 0) {
            LogUtil.Auth.tokenRefreshFailed(log, "用户不存在或已被禁用");
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        userProfileMapper.selectById(userId);

        // 获取旧的 Token
        String oldAccessToken = tokenStorageService.getAccessToken(userId);
        String oldRefreshToken = tokenStorageService.getRefreshToken(userId);

        // 生成新的 Token
        TokenPair newTokens = generateTokens(sysUser);

        // 更新 Redis 中的 Token
        long accessTtl = RedisKey.Token.getAccessTokenTtlSeconds(jwtProperties.getAccessTokenExpiration());
        long refreshTtl = RedisKey.Token.getRefreshTokenTtlSeconds(jwtProperties.getRefreshTokenExpiration());
        tokenStorageService.updateTokens(
                userId,
                oldAccessToken,
                newTokens.accessToken(),
                oldRefreshToken,
                newTokens.refreshToken(),
                accessTtl,
                refreshTtl
        );

        // 构建返回结果
        LoginVO loginVO = buildLoginVO(sysUser, newTokens.accessToken(), newTokens.refreshToken());

        LogUtil.Auth.tokenRefreshSuccess(log, userId);
        return loginVO;
    }

    // ==================== 私有方法 ====================

    /**
     * 根据邮箱查找用户
     */
    private SysUser findUserByEmail(String email) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getEmail, email);
        SysUser sysUser = sysUserMapper.selectOne(wrapper);

        if (sysUser == null) {
            LogUtil.Auth.loginFailed(log, email, "用户不存在");
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        return sysUser;
    }

    /**
     * 检查邮箱是否存在
     */
    private boolean isEmailExists(String email) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getEmail, email);
        return sysUserMapper.selectCount(wrapper) > 0;
    }

    /**
     * 检查用户名是否存在
     */
    private boolean isUsernameExists(String username) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, username);
        return sysUserMapper.selectCount(wrapper) > 0;
    }

    /**
     * 创建系统用户
     */
    private SysUser createSysUser(RegisterDTO registerDTO) {
        SysUser sysUser = new SysUser();
        sysUser.setUsername(registerDTO.getUsername());
        sysUser.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        sysUser.setEmail(registerDTO.getEmail());
        sysUser.setNickname(registerDTO.getNickname() != null ? registerDTO.getNickname() : registerDTO.getUsername());
        sysUser.setStatus(1);
        return sysUser;
    }

    /**
     * 创建用户档案
     */
    private UserProfile createUserProfile(SysUser sysUser) {
        UserProfile profile = new UserProfile();
        profile.setId(sysUser.getId());
        profile.setUsername(sysUser.getUsername());
        profile.setDisplayName(sysUser.getNickname());
        profile.setStatus("offline");
        profile.setLastSeenAt(LocalDateTime.now());
        profile.setCreateTime(LocalDateTime.now());
        profile.setUpdateTime(LocalDateTime.now());
        profile.setDeleted(0);
        return profile;
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
     * 构建登录返回结果
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
}
