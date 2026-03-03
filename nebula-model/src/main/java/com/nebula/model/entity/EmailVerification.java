package com.nebula.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.OffsetDateTime;

/**
 * 邮箱验证实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("email_verifications")
@Schema(description = "邮箱验证")
public class EmailVerification extends BaseEntity {

    @TableId(type = IdType.AUTO)
    @Schema(description = "验证ID")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "邮箱地址")
    private String email;

    @Schema(description = "验证令牌")
    private String token;

    @Schema(description = "验证类型(registration-注册验证,email_change-邮箱修改,password_reset-密码重置)")
    private String type;

    @Schema(description = "状态(pending-待验证,verified-已验证,expired-已过期)")
    private String status;

    @Schema(description = "验证时间")
    private OffsetDateTime verifiedAt;

    @Schema(description = "过期时间")
    private OffsetDateTime expiresAt;
}
