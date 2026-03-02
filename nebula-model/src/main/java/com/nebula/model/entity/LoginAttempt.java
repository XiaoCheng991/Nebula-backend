package com.nebula.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 登录尝试实体（防止暴力破解）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("login_attempts")
@Schema(description = "登录尝试记录")
public class LoginAttempt extends BaseEntity {

    @TableId(type = IdType.AUTO)
    @Schema(description = "记录ID")
    private Long id;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "IP地址")
    private String ipAddress;

    @Schema(description = "尝试次数")
    private Integer attemptCount;

    @Schema(description = "最后尝试时间")
    private LocalDateTime lastAttemptAt;

    @Schema(description = "锁定至")
    private LocalDateTime lockedUntil;
}
