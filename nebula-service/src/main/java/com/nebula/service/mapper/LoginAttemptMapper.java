package com.nebula.service.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nebula.model.entity.LoginAttempt;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.Optional;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 登录尝试Mapper
 */
@Mapper
public interface LoginAttemptMapper extends BaseMapper<LoginAttempt> {

    /**
     * 根据用户名和IP查找记录
     */
    @Select("SELECT * FROM login_attempts WHERE username = #{username} AND ip_address = #{ipAddress} AND deleted = 0")
    Optional<LoginAttempt> findByUsernameAndIp(@Param("username") String username, @Param("ipAddress") String ipAddress);

    /**
     * 查找IP的锁定记录
     */
    @Select("SELECT * FROM login_attempts WHERE ip_address = #{ipAddress} AND locked_until > NOW() AND deleted = 0 ORDER BY locked_until DESC LIMIT 1")
    Optional<LoginAttempt> findActiveLockByIp(@Param("ipAddress") String ipAddress);

    /**
     * 增加尝试次数
     */
    @Update("UPDATE login_attempts SET attempt_count = attempt_count + 1, last_attempt_at = NOW(), update_time = NOW() WHERE id = #{id} AND deleted = 0")
    int incrementAttemptCount(@Param("id") Long id);

    /**
     * 锁定账户
     */
    @Update("UPDATE login_attempts SET locked_until = #{lockedUntil}, update_time = NOW() WHERE id = #{id} AND deleted = 0")
    int lockAccount(@Param("id") Long id, @Param("lockedUntil") LocalDateTime lockedUntil);

    /**
     * 清除锁定
     */
    @Update("UPDATE login_attempts SET attempt_count = 0, locked_until = NULL, update_time = NOW() WHERE id = #{id} AND deleted = 0")
    int clearLock(@Param("id") Long id);

    /**
     * 清理过期的锁定记录
     */
    @Update("UPDATE login_attempts SET deleted = 1 WHERE locked_until < #{beforeTime} AND deleted = 0")
    int cleanupExpiredLocks(@Param("beforeTime") LocalDateTime beforeTime);
}
