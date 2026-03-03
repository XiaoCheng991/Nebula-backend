package com.nebula.service.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nebula.model.entity.LoginLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * 登录日志Mapper
 */
@Mapper
public interface LoginLogMapper extends BaseMapper<LoginLog> {

    /**
     * 分页查询用户的登录日志
     */
    @Select("SELECT * FROM login_logs WHERE user_id = #{userId} AND deleted = 0 ORDER BY login_at DESC")
    IPage<LoginLog> selectPageByUserId(Page<LoginLog> page, @Param("userId") Long userId);

    /**
     * 查询用户的最近登录记录
     */
    @Select("SELECT * FROM login_logs WHERE user_id = #{userId} AND status = 'success' AND deleted = 0 ORDER BY login_at DESC LIMIT #{limit}")
    List<LoginLog> selectRecentByUserId(@Param("userId") Long userId, @Param("limit") int limit);

    /**
     * 查询IP的最近失败登录次数
     */
    @Select("SELECT COUNT(*) FROM login_logs WHERE ip_address = #{ipAddress} AND status = 'failed' AND login_at > #{since} AND deleted = 0")
    int countFailedAttemptsByIp(@Param("ipAddress") String ipAddress, @Param("since") OffsetDateTime since);

    /**
     * 查询用户的最近失败登录次数
     */
    @Select("SELECT COUNT(*) FROM login_logs WHERE user_id = #{userId} AND status = 'failed' AND login_at > #{since} AND deleted = 0")
    int countFailedAttemptsByUser(@Param("userId") Long userId, @Param("since") OffsetDateTime since);

    /**
     * 查询指定时间范围内的登录统计
     */
    @Select("SELECT COUNT(*) FROM login_logs WHERE login_at BETWEEN #{startTime} AND #{endTime} AND deleted = 0")
    int countByTimeRange(@Param("startTime") OffsetDateTime startTime, @Param("endTime") OffsetDateTime endTime);

    /**
     * 按登录类型统计
     */
    @Select("SELECT login_type, COUNT(*) as count FROM login_logs WHERE login_at > #{since} AND deleted = 0 GROUP BY login_type")
    List<java.util.Map<String, Object>> countByLoginType(@Param("since") OffsetDateTime since);

    /**
     * 清理旧的登录日志
     */
    @Select("UPDATE login_logs SET deleted = 1 WHERE login_at < #{beforeTime} AND deleted = 0")
    int cleanupOldLogs(@Param("beforeTime") OffsetDateTime beforeTime);
}
