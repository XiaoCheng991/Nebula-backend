package com.nebula.service.service;


/**
 * 登录尝试服务接口
 */
public interface LoginAttemptService {

    /**
     * 记录登录尝试
     */
    void recordAttempt(String username, String ipAddress, boolean success);

    /**
     * 检查是否被锁定
     */
    boolean isLocked(String username, String ipAddress);

    /**
     * 获取剩余尝试次数
     */
    int getRemainingAttempts(String username, String ipAddress);

    /**
     * 解锁账户
     */
    void unlockAccount(String username, String ipAddress);

    /**
     * 清理过期锁定记录
     */
    void cleanupExpiredLocks();
}
