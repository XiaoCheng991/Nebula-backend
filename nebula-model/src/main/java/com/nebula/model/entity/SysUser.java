package com.nebula.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.OffsetDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_users")
@Schema(description = "系统用户")
public class SysUser extends BaseEntity {

    @TableId(type = IdType.AUTO)
    @Schema(description = "用户ID")
    private Long id;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "密码")
    private String password;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "显示名称")
    private String displayName;

    @Schema(description = "头像文件名称")
    private String avatarName;

    @Schema(description = "头像在MinIO中的URL")
    private String avatarUrl;

    @Schema(description = "头像文件大小（字节）")
    private Long avatarSize;

    @Schema(description = "个人简介")
    private String bio;

    @Schema(description = "在线状态（online/offline/busy/away）")
    private String onlineStatus;

    @Schema(description = "账号状态（0-禁用，1-启用）")
    private Integer accountStatus;

    @Schema(description = "最后登录时间")
    private OffsetDateTime lastLoginAt;

    @Schema(description = "最后活跃时间")
    private OffsetDateTime lastSeenAt;
}
