package com.nebula.service.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nebula.model.entity.PasswordResetToken;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.OffsetDateTime;
import java.util.Optional;

/**
 * 密码重置令牌Mapper
 */
@Mapper
public interface PasswordResetTokenMapper extends BaseMapper<PasswordResetToken> {

    /**
     * 根据令牌查询
     */
    @Select("SELECT * FROM password_reset_tokens WHERE token = #{token} AND deleted = 0")
    Optional<PasswordResetToken> findByToken(@Param("token") String token);

    /**
     * 查询用户最新的待使用令牌
     */
    @Select("SELECT * FROM password_reset_tokens WHERE user_id = #{userId} AND status = 'pending' AND deleted = 0 ORDER BY create_time DESC LIMIT 1")
    Optional<PasswordResetToken> findLatestPendingByUser(@Param("userId") Long userId);

    /**
     * 使用令牌
     */
    @Select("UPDATE password_reset_tokens SET status = 'used', used_at = NOW(), update_time = NOW() WHERE id = #{id} AND status = 'pending' AND deleted = 0")
    int markAsUsed(@Param("id") Long id);

    /**
     * 使用户的所有待使用令牌过期
     */
    @Select("UPDATE password_reset_tokens SET status = 'expired', update_time = NOW() WHERE user_id = #{userId} AND status = 'pending' AND deleted = 0")
    void expirePendingByUser(@Param("userId") Long userId);

    /**
     * 清理过期的令牌记录
     */
    @Select("UPDATE password_reset_tokens SET deleted = 1 WHERE status IN ('used', 'expired') AND update_time < #{beforeTime} AND deleted = 0")
    int cleanupOldRecords(@Param("beforeTime") OffsetDateTime beforeTime);
}
