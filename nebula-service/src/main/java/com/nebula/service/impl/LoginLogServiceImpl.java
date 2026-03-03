package com.nebula.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nebula.model.entity.LoginLog;
import com.nebula.service.mapper.LoginLogMapper;
import com.nebula.service.service.LoginLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

/**
 * 登录日志服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LoginLogServiceImpl implements LoginLogService {

    private final LoginLogMapper loginLogMapper;

    // 日志保留天数
    private static final int LOG_RETENTION_DAYS = 90;

    @Override
    public void recordLoginLog(LoginLog loginLog) {
        loginLogMapper.insert(loginLog);
    }

    @Override
    public IPage<LoginLog> getUserLoginLogs(Long userId, Integer pageNum, Integer pageSize) {
        Page<LoginLog> page = new Page<>(pageNum, pageSize);
        return loginLogMapper.selectPageByUserId(page, userId);
    }

    @Override
    public List<LoginLog> getRecentLoginLogs(Long userId, int limit) {
        return loginLogMapper.selectRecentByUserId(userId, limit);
    }

    @Override
    public LoginLog getLastLoginLog(Long userId) {
        List<LoginLog> logs = loginLogMapper.selectRecentByUserId(userId, 1);
        return logs.isEmpty() ? null : logs.get(0);
    }

    @Override
    public int countFailedAttemptsByIp(String ipAddress, OffsetDateTime since) {
        return loginLogMapper.countFailedAttemptsByIp(ipAddress, since);
    }

    @Override
    public int countFailedAttemptsByUser(Long userId, OffsetDateTime since) {
        return loginLogMapper.countFailedAttemptsByUser(userId, since);
    }

    @Override
    public Map<String, Object> getLoginStatistics(OffsetDateTime startTime, OffsetDateTime endTime) {
        int totalCount = loginLogMapper.countByTimeRange(startTime, endTime);
        List<Map<String, Object>> typeStats = loginLogMapper.countByLoginType(startTime);

        Map<String, Object> statistics = new java.util.HashMap<>();
        statistics.put("totalCount", totalCount);
        statistics.put("typeStats", typeStats);

        return statistics;
    }

    @Override
    @Scheduled(cron = "0 0 2 * * ?") // 每天凌晨2点执行
    public void cleanupOldLogs() {
        OffsetDateTime beforeTime = OffsetDateTime.now().minusDays(LOG_RETENTION_DAYS);
        int count = loginLogMapper.cleanupOldLogs(beforeTime);
        log.info("清理登录日志: {} 条（{}天前的记录）", count, LOG_RETENTION_DAYS);
    }

    @Override
    public void recordLogout(Long userId, String tokenId) {
        LambdaQueryWrapper<LoginLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LoginLog::getUserId, userId)
                .eq(LoginLog::getTokenId, tokenId)
                .isNull(LoginLog::getLogoutAt)
                .orderByDesc(LoginLog::getLoginAt)
                .last("LIMIT 1");

        LoginLog loginLog = loginLogMapper.selectOne(wrapper);
        if (loginLog != null) {
            loginLog.setLogoutAt(OffsetDateTime.now());
            loginLog.setUpdateTime(OffsetDateTime.now());
            loginLogMapper.updateById(loginLog);
        }
    }
}
