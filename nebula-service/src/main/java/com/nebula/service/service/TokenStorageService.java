package com.nebula.service.service;

import com.nebula.model.vo.LoginVO;

/**
 * Token 存储服务
 * 负责 Token 在 Redis 中的统一管理
 */
public interface TokenStorageService {

    /**
     * 保存 Token 到 Redis
     *
     * @param userId      用户ID
     * @param accessToken  Access Token
     * @param refreshToken Refresh Token
     * @param accessTtl    Access Token 过期时间（秒）
     * @param refreshTtl   Refresh Token 过期时间（秒）
     */
    void saveTokens(Long userId, String accessToken, String refreshToken, long accessTtl, long refreshTtl);

    /**
     * 获取用户当前的 Access Token
     *
     * @param userId 用户ID
     * @return Access Token，不存在返回 null
     */
    String getAccessToken(Long userId);

    /**
     * 根据 Access Token 获取用户ID
     *
     * @param accessToken Access Token
     * @return 用户ID，不存在返回 null
     */
    Long getUserIdByAccessToken(String accessToken);

    /**
     * 获取用户当前的 Refresh Token
     *
     * @param userId 用户ID
     * @return Refresh Token，不存在返回 null
     */
    String getRefreshToken(Long userId);

    /**
     * 根据 Refresh Token 获取用户ID
     *
     * @param refreshToken Refresh Token
     * @return 用户ID，不存在返回 null
     */
    Long getUserIdByRefreshToken(String refreshToken);

    /**
     * 验证 Access Token 是否有效
     *
     * @param accessToken Access Token
     * @return 是否有效
     */
    boolean validateAccessToken(String accessToken);

    /**
     * 验证 Refresh Token 是否有效
     *
     * @param refreshToken Refresh Token
     * @return 是否有效
     */
    boolean validateRefreshToken(String refreshToken);

    /**
     * 删除用户的所有 Token（登出）
     *
     * @param userId 用户ID
     */
    void deleteTokens(Long userId);

    /**
     * 删除指定的 Token 对
     *
     * @param userId        用户ID
     * @param accessToken   Access Token
     * @param refreshToken  Refresh Token
     */
    void deleteTokens(Long userId, String accessToken, String refreshToken);

    /**
     * 更新用户的 Token（刷新 Token 时使用）
     *
     * @param userId          用户ID
     * @param oldAccessToken  旧的 Access Token
     * @param newAccessToken  新的 Access Token
     * @param oldRefreshToken 旧的 Refresh Token
     * @param newRefreshToken 新的 Refresh Token
     * @param accessTtl       Access Token 过期时间（秒）
     * @param refreshTtl      Refresh Token 过期时间（秒）
     */
    void updateTokens(Long userId, String oldAccessToken, String newAccessToken,
                      String oldRefreshToken, String newRefreshToken,
                      long accessTtl, long refreshTtl);
}
