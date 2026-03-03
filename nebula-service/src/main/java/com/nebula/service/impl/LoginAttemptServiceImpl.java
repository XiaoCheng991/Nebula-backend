package com.nebula.service.impl;

import com.nebula.model.entity.LoginAttempt;
import com.nebula.service.mapper.LoginAttemptMapper;
import com.nebula.service.service.LoginAttemptService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Optional;

/**
 * 登录尝试服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LoginAttemptServiceImpl implements LoginAttemptService {

    private final LoginAttemptMapper loginAttemptMapper;

    // 配置参数
    private static final int MAX_ATTEMPTS = 5;
    private static final int LOCK_DURATION_MINUTES = 30;
    private static final int ATTEMPT_WINDOW_MINUTES = 5;

    @Override
    public void recordAttempt(String username, String ipAddress, boolean success) {
        Optional<LoginAttempt> attemptOpt = loginAttemptMapper.findByUsernameAndIp(username, ipAddress);

        if (attemptOpt.isPresent()) {
            LoginAttempt attempt = attemptOpt.get();

            if (success) {
                // 登录成功，清除失败记录
                loginAttemptMapper.clearLock(attempt.getId());
                log.debug("登录成功，清除失败记录: username={}, ip={}", username, ipAddress);
            } else {
                // 登录失败，增加计数
                loginAttemptMapper.incrementAttemptCount(attempt.getId());

                // 检查是否需要锁定
                if (attempt.getAttemptCount() + 1 >= MAX_ATTEMPTS) {
                    OffsetDateTime lockedUntil = OffsetDateTime.now().plusMinutes(LOCK_DURATION_MINUTES);
                    loginAttemptMapper.lockAccount(attempt.getId(), lockedUntil);
                    log.warn("账户已锁定: username={}, ip={}, lockedUntil={}", username, ipAddress, lockedUntil);
                }
            }
        } else {
            // 新建记录
            if (!success) {
                LoginAttempt attempt = new LoginAttempt();
                attempt.setUsername(username);
                attempt.setIpAddress(ipAddress);
                attempt.setAttemptCount(1);
                attempt.setLastAttemptAt(OffsetDateTime.now());
                attempt.setCreateTime(OffsetDateTime.now());
                attempt.setUpdateTime(OffsetDateTime.now());
                attempt.setDeleted(0);

                loginAttemptMapper.insert(attempt);
            }
        }
    }

    @Override
    public boolean isLocked(String username, String ipAddress) {
        // 检查IP是否被锁定
        Optional<LoginAttempt> ipLockOpt = loginAttemptMapper.findActiveLockByIp(ipAddress);
        if (ipLockOpt.isPresent()) {
            LoginAttempt lock = ipLockOpt.get();
            if (lock.getLockedUntil().isAfter(OffsetDateTime.now())) {
                log.warn("IP已被锁定: ip={}, lockedUntil={}", ipAddress, lock.getLockedUntil());
                return true;
            }
        }

        // 检查用户名是否被锁定
        Optional<LoginAttempt> attemptOpt = loginAttemptMapper.findByUsernameAndIp(username, ipAddress);
        if (attemptOpt.isPresent()) {
            LoginAttempt attempt = attemptOpt.get();
            if (attempt.getLockedUntil() != null && attempt.getLockedUntil().isAfter(OffsetDateTime.now())) {
                log.warn("账户已被锁定: username={}, lockedUntil={}", username, attempt.getLockedUntil());
                return true;
            }

            // 检查尝试次数
            if (attempt.getAttemptCount() >= MAX_ATTEMPTS) {
                // 检查是否在时间窗口内
                if (attempt.getLastAttemptAt() != null &&
                        attempt.getLastAttemptAt().isAfter(OffsetDateTime.now().minusMinutes(ATTEMPT_WINDOW_MINUTES))) {
                    // 锁定账户
                    OffsetDateTime lockedUntil = OffsetDateTime.now().plusMinutes(LOCK_DURATION_MINUTES);
                    loginAttemptMapper.lockAccount(attempt.getId(), lockedUntil);
                    log.warn("账户已锁定: username={}, lockedUntil={}", username, lockedUntil);
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public int getRemainingAttempts(String username, String ipAddress) {
        Optional<LoginAttempt> attemptOpt = loginAttemptMapper.findByUsernameAndIp(username, ipAddress);
        if (attemptOpt.isPresent()) {
            LoginAttempt attempt = attemptOpt.get();
            if (attempt.getAttemptCount() >= MAX_ATTEMPTS) {
                return 0;
            }
            return MAX_ATTEMPTS - attempt.getAttemptCount();
        }
        return MAX_ATTEMPTS;
    }

    @Override
    public void unlockAccount(String username, String ipAddress) {
        Optional<LoginAttempt> attemptOpt = loginAttemptMapper.findByUsernameAndIp(username, ipAddress);
        if (attemptOpt.isPresent()) {
            loginAttemptMapper.clearLock(attemptOpt.get().getId());
            log.info("账户已解锁: username={}, ip={}", username, ipAddress);
        }
    }

    @Override
    @Scheduled(cron = "0 0 3 * * ?") // 每天凌晨3点执行
    public void cleanupExpiredLocks() {
        OffsetDateTime beforeTime = OffsetDateTime.now().minusDays(7);
        int count = loginAttemptMapper.cleanupExpiredLocks(beforeTime);
        log.info("清理过期锁定记录: {} 条", count);
    }
}
