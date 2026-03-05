package com.nebula.model.entity.im;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.OffsetDateTime;

@Data
@TableName("im_user_ban")
@Schema(description = "用户封禁表")
public class ImUserBan implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    @Schema(description = "封禁ID")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "封禁类型：CHAT-禁言，LOGIN-禁止登录，FULL-全封禁")
    private String banType;

    @Schema(description = "封禁原因")
    private String reason;

    @Schema(description = "封禁时间")
    private OffsetDateTime banTime;

    @Schema(description = "过期时间")
    private OffsetDateTime expireTime;

    @Schema(description = "是否永久封禁")
    private Boolean isPermanent;

    @Schema(description = "操作者ID")
    private Long operatorId;

    @Schema(description = "操作者名称")
    private String operatorName;

    @Schema(description = "是否有效")
    private Boolean isActive;

    @Schema(description = "解封时间")
    private OffsetDateTime unbanTime;

    @Schema(description = "解封原因")
    private String unbanReason;

    @Schema(description = "创建时间")
    private OffsetDateTime createTime;

    @Schema(description = "更新时间")
    private OffsetDateTime updateTime;
}
