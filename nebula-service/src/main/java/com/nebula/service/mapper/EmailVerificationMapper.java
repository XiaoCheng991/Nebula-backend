package com.nebula.service.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nebula.model.entity.EmailVerification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.OffsetDateTime;
import java.util.Optional;

/**
 * 邮箱验证Mapper
 */
@Mapper
public interface EmailVerificationMapper extends BaseMapper<EmailVerification> {

    /**
     * 根据令牌查询验证记录
     */
    @Select("SELECT * FROM email_verifications WHERE token = #{token} AND deleted = 0")
    Optional<EmailVerification> findByToken(@Param("token") String token);

    /**
     * 查询用户最新的待验证记录
     */
    @Select("SELECT * FROM email_verifications WHERE user_id = #{userId} AND type = #{type} AND status = 'pending' AND deleted = 0 ORDER BY create_time DESC LIMIT 1")
    Optional<EmailVerification> findLatestPendingByUserAndType(@Param("userId") Long userId, @Param("type") String type);

    /**
     * 使用户的所有待验证记录过期
     */
    @Select("UPDATE email_verifications SET status = 'expired', update_time = NOW() WHERE user_id = #{userId} AND type = #{type} AND status = 'pending' AND deleted = 0")
    void expirePendingByUserAndType(@Param("userId") Long userId, @Param("type") String type);

    /**
     * 清理过期的验证记录
     */
    @Select("UPDATE email_verifications SET deleted = 1 WHERE status = 'expired' AND expires_at < #{beforeTime} AND deleted = 0")
    int cleanupExpiredRecords(@Param("beforeTime") OffsetDateTime beforeTime);
}
