package com.nebula.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 密码重置令牌实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("password_reset_tokens")
@Schema(description = "密码重置令牌")
public class PasswordResetToken extends BaseEntity {

    @TableId(type = IdType.AUTO)
    @Schema(description = "令牌ID")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "邮箱地址")
    private String email;

    @Schema(description = "重置令牌")
    private String token;

    @Schema(description = "状态(pending-待使用,used-已使用,expired-已过期)")
    private String status;

    @Schema(description = "使用时间")
    private LocalDateTime usedAt;

    @Schema(description = "过期时间")
    private LocalDateTime expiresAt;

    @Schema(description = "请求IP")
    private String ipAddress;

    @Schema(description = "User-Agent")
    private String userAgent;
}
