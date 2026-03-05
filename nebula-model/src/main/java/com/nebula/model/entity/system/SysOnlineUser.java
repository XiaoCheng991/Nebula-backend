package com.nebula.model.entity.system;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.OffsetDateTime;

@Data
@TableName("sys_online_user")
@Schema(description = "在线用户表")
public class SysOnlineUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "Token")
    private String token;

    @Schema(description = "IP地址")
    private String ipAddress;

    @Schema(description = "地理位置")
    private String location;

    @Schema(description = "浏览器")
    private String browser;

    @Schema(description = "操作系统")
    private String os;

    @Schema(description = "登录时间")
    private OffsetDateTime loginTime;

    @Schema(description = "最后活动时间")
    private OffsetDateTime lastActivityTime;

    @Schema(description = "是否已过期")
    private Boolean expired;
}
