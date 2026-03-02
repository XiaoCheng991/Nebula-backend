package com.nebula.service.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.nebula.model.entity.LoginLog;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 登录日志服务接口
 */
public interface LoginLogService {

    /**
     * 记录登录日志
     */
    void recordLoginLog(LoginLog loginLog);

    /**
     * 获取用户登录日志
     */
    IPage<LoginLog> getUserLoginLogs(Long userId, Integer pageNum, Integer pageSize);

    /**
     * 获取最近登录日志
     */
    List<LoginLog> getRecentLoginLogs(Long userId, int limit);

    /**
     * 获取最后一次登录日志
     */
    LoginLog getLastLoginLog(Long userId);

    /**
     * 统计IP失败尝试次数
     */
    int countFailedAttemptsByIp(String ipAddress, LocalDateTime since);

    /**
     * 统计用户失败尝试次数
     */
    int countFailedAttemptsByUser(Long userId, LocalDateTime since);

    /**
     * 获取登录统计
     */
    Map<String, Object> getLoginStatistics(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 清理旧日志
     */
    void cleanupOldLogs();

    /**
     * 记录登出
     */
    void recordLogout(Long userId, String tokenId);
}
