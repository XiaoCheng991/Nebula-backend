package com.nebula.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 登录日志实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("login_logs")
@Schema(description = "登录日志")
public class LoginLog extends BaseEntity {

    @TableId(type = IdType.AUTO)
    @Schema(description = "日志ID")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "登录类型(password-密码,github-GitHub,google-谷歌,wechat-微信)")
    private String loginType;

    @Schema(description = "状态(success-成功,failed-失败,locked-锁定)")
    private String status;

    @Schema(description = "失败原因")
    private String failReason;

    @Schema(description = "IP地址")
    private String ipAddress;

    @Schema(description = "IP归属地")
    private String ipLocation;

    @Schema(description = "User-Agent")
    private String userAgent;

    @Schema(description = "浏览器")
    private String browser;

    @Schema(description = "操作系统")
    private String os;

    @Schema(description = "设备")
    private String device;

    @Schema(description = "登录时间")
    private LocalDateTime loginAt;

    @Schema(description = "登出时间")
    private LocalDateTime logoutAt;

    @Schema(description = "令牌ID")
    private String tokenId;
}
