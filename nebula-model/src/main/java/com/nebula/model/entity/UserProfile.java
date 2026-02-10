package com.nebula.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 用户档案实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user_profiles")
@Schema(description = "用户档案")
public class UserProfile extends BaseEntity {

    @TableId(type = IdType.AUTO)
    @Schema(description = "用户ID")
    private Long id;

    @Schema(description = "用户名")
    private String username;

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

    @Schema(description = "在线状态")
    private String status;

    @Schema(description = "最后活跃时间")
    private LocalDateTime lastSeenAt;
}
