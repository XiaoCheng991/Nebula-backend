package com.nebula.service.impl;

import com.nebula.common.constant.RedisKey;
import com.nebula.common.exception.BusinessException;
import com.nebula.common.exception.ErrorCode;
import com.nebula.common.util.LogUtil;
import com.nebula.service.service.TokenStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Token 存储服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TokenStorageServiceImpl implements TokenStorageService {

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void saveTokens(Long userId, String accessToken, String refreshToken, long accessTtl, long refreshTtl) {
        try {
            // 保存 Access Token（按用户索引）
            String accessTokenByUserKey = RedisKey.Token.accessTokenByUser(userId);
            redisTemplate.opsForValue().set(accessTokenByUserKey, accessToken, accessTtl, TimeUnit.SECONDS);
            LogUtil.Redis.set(log, accessTokenByUserKey, accessTtl);

            // 保存 Access Token（按 Token 索引）
            String userByAccessTokenKey = RedisKey.Token.userByAccessToken(accessToken);
            redisTemplate.opsForValue().set(userByAccessTokenKey, userId, accessTtl, TimeUnit.SECONDS);
            LogUtil.Redis.set(log, userByAccessTokenKey, accessTtl);

            // 保存 Refresh Token（按用户索引）
            String refreshTokenByUserKey = RedisKey.Token.refreshTokenByUser(userId);
            redisTemplate.opsForValue().set(refreshTokenByUserKey, refreshToken, refreshTtl, TimeUnit.SECONDS);
            LogUtil.Redis.set(log, refreshTokenByUserKey, refreshTtl);

            // 保存 Refresh Token（按 Token 索引）
            String userByRefreshTokenKey = RedisKey.Token.userByRefreshToken(refreshToken);
            redisTemplate.opsForValue().set(userByRefreshTokenKey, userId, refreshTtl, TimeUnit.SECONDS);
            LogUtil.Redis.set(log, userByRefreshTokenKey, refreshTtl);

            log.debug("Token保存成功 | userId={}", userId);
        } catch (Exception e) {
            LogUtil.Redis.error(log, "saveTokens", "userId:" + userId, e.getMessage());
            throw new BusinessException(ErrorCode.CACHE_SET_FAILED, e);
        }
    }

    @Override
    public String getAccessToken(Long userId) {
        try {
            String key = RedisKey.Token.accessTokenByUser(userId);
            String token = (String) redisTemplate.opsForValue().get(key);
            LogUtil.Redis.get(log, key, token != null);
            return token;
        } catch (Exception e) {
            LogUtil.Redis.error(log, "getAccessToken", "userId:" + userId, e.getMessage());
            return null;
        }
    }

    @Override
    public Long getUserIdByAccessToken(String accessToken) {
        try {
            String key = RedisKey.Token.userByAccessToken(accessToken);
            Object userIdObj = redisTemplate.opsForValue().get(key);
            LogUtil.Redis.get(log, key, userIdObj != null);
            return userIdObj != null ? Long.valueOf(userIdObj.toString()) : null;
        } catch (Exception e) {
            LogUtil.Redis.error(log, "getUserIdByAccessToken", "token:" + LogUtil.Utils.maskToken(accessToken), e.getMessage());
            return null;
        }
    }

    @Override
    public String getRefreshToken(Long userId) {
        try {
            String key = RedisKey.Token.refreshTokenByUser(userId);
            String token = (String) redisTemplate.opsForValue().get(key);
            LogUtil.Redis.get(log, key, token != null);
            return token;
        } catch (Exception e) {
            LogUtil.Redis.error(log, "getRefreshToken", "userId:" + userId, e.getMessage());
            return null;
        }
    }

    @Override
    public Long getUserIdByRefreshToken(String refreshToken) {
        try {
            String key = RedisKey.Token.userByRefreshToken(refreshToken);
            Object userIdObj = redisTemplate.opsForValue().get(key);
            LogUtil.Redis.get(log, key, userIdObj != null);
            return userIdObj != null ? Long.valueOf(userIdObj.toString()) : null;
        } catch (Exception e) {
            LogUtil.Redis.error(log, "getUserIdByRefreshToken", "token:" + LogUtil.Utils.maskToken(refreshToken), e.getMessage());
            return null;
        }
    }

    @Override
    public boolean validateAccessToken(String accessToken) {
        try {
            Long userId = getUserIdByAccessToken(accessToken);
            if (userId == null) {
                return false;
            }

            // 再次确认该用户会话仍然存在且 token 一致
            String cachedToken = getAccessToken(userId);
            return accessToken.equals(cachedToken);
        } catch (Exception e) {
            LogUtil.Redis.error(log, "validateAccessToken", "token:" + LogUtil.Utils.maskToken(accessToken), e.getMessage());
            return false;
        }
    }

    @Override
    public boolean validateRefreshToken(String refreshToken) {
        try {
            Long userId = getUserIdByRefreshToken(refreshToken);
            if (userId == null) {
                return false;
            }

            // 再次确认该用户 refresh token 一致
            String cachedToken = getRefreshToken(userId);
            return refreshToken.equals(cachedToken);
        } catch (Exception e) {
            LogUtil.Redis.error(log, "validateRefreshToken", "token:" + LogUtil.Utils.maskToken(refreshToken), e.getMessage());
            return false;
        }
    }

    @Override
    public void deleteTokens(Long userId) {
        try {
            // 获取当前的 token
            String accessToken = getAccessToken(userId);
            String refreshToken = getRefreshToken(userId);

            // 删除 Access Token（按用户索引）
            String accessTokenByUserKey = RedisKey.Token.accessTokenByUser(userId);
            redisTemplate.delete(accessTokenByUserKey);
            LogUtil.Redis.delete(log, accessTokenByUserKey);

            // 删除 Access Token（按 Token 索引）
            if (accessToken != null) {
                String userByAccessTokenKey = RedisKey.Token.userByAccessToken(accessToken);
                redisTemplate.delete(userByAccessTokenKey);
                LogUtil.Redis.delete(log, userByAccessTokenKey);
            }

            // 删除 Refresh Token（按用户索引）
            String refreshTokenByUserKey = RedisKey.Token.refreshTokenByUser(userId);
            redisTemplate.delete(refreshTokenByUserKey);
            LogUtil.Redis.delete(log, refreshTokenByUserKey);

            // 删除 Refresh Token（按 Token 索引）
            if (refreshToken != null) {
                String userByRefreshTokenKey = RedisKey.Token.userByRefreshToken(refreshToken);
                redisTemplate.delete(userByRefreshTokenKey);
                LogUtil.Redis.delete(log, userByRefreshTokenKey);
            }

            LogUtil.Auth.logout(log, userId);
        } catch (Exception e) {
            LogUtil.Redis.error(log, "deleteTokens", "userId:" + userId, e.getMessage());
            throw new BusinessException(ErrorCode.CACHE_DELETE_FAILED, e);
        }
    }

    @Override
    public void deleteTokens(Long userId, String accessToken, String refreshToken) {
        try {
            // 删除 Access Token（按用户索引）
            String accessTokenByUserKey = RedisKey.Token.accessTokenByUser(userId);
            redisTemplate.delete(accessTokenByUserKey);
            LogUtil.Redis.delete(log, accessTokenByUserKey);

            // 删除 Access Token（按 Token 索引）
            if (accessToken != null) {
                String userByAccessTokenKey = RedisKey.Token.userByAccessToken(accessToken);
                redisTemplate.delete(userByAccessTokenKey);
                LogUtil.Redis.delete(log, userByAccessTokenKey);
            }

            // 删除 Refresh Token（按用户索引）
            String refreshTokenByUserKey = RedisKey.Token.refreshTokenByUser(userId);
            redisTemplate.delete(refreshTokenByUserKey);
            LogUtil.Redis.delete(log, refreshTokenByUserKey);

            // 删除 Refresh Token（按 Token 索引）
            if (refreshToken != null) {
                String userByRefreshTokenKey = RedisKey.Token.userByRefreshToken(refreshToken);
                redisTemplate.delete(userByRefreshTokenKey);
                LogUtil.Redis.delete(log, userByRefreshTokenKey);
            }

            LogUtil.Auth.logout(log, userId);
        } catch (Exception e) {
            LogUtil.Redis.error(log, "deleteTokens", "userId:" + userId, e.getMessage());
            throw new BusinessException(ErrorCode.CACHE_DELETE_FAILED, e);
        }
    }

    @Override
    public void updateTokens(Long userId, String oldAccessToken, String newAccessToken,
                             String oldRefreshToken, String newRefreshToken,
                             long accessTtl, long refreshTtl) {
        try {
            // 先删除旧的 token
            if (oldAccessToken != null) {
                String oldUserByAccessTokenKey = RedisKey.Token.userByAccessToken(oldAccessToken);
                redisTemplate.delete(oldUserByAccessTokenKey);
                LogUtil.Redis.delete(log, oldUserByAccessTokenKey);
            }

            if (oldRefreshToken != null) {
                String oldUserByRefreshTokenKey = RedisKey.Token.userByRefreshToken(oldRefreshToken);
                redisTemplate.delete(oldUserByRefreshTokenKey);
                LogUtil.Redis.delete(log, oldUserByRefreshTokenKey);
            }

            // 保存新的 token
            saveTokens(userId, newAccessToken, newRefreshToken, accessTtl, refreshTtl);

            log.debug("Token更新成功 | userId={}", userId);
        } catch (Exception e) {
            LogUtil.Redis.error(log, "updateTokens", "userId:" + userId, e.getMessage());
            throw new BusinessException(ErrorCode.CACHE_SET_FAILED, e);
        }
    }
}
