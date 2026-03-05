package com.nebula.model.entity.im;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.OffsetDateTime;

@Data
@TableName("im_message_archive")
@Schema(description = "消息归档表")
public class ImMessageArchive implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    @Schema(description = "归档ID")
    private Long id;

    @Schema(description = "消息ID")
    private Long messageId;

    @Schema(description = "聊天室ID")
    private Long roomId;

    @Schema(description = "发送者ID")
    private Long senderId;

    @Schema(description = "发送者名称")
    private String senderName;

    @Schema(description = "消息类型")
    private String messageType;

    @Schema(description = "消息内容")
    private String content;

    @Schema(description = "是否包含敏感词")
    private Boolean isSensitive;

    @Schema(description = "命中的敏感词(JSON数组)")
    private String sensitiveHits;

    @Schema(description = "是否已撤回")
    private Boolean isRecalled;

    @Schema(description = "撤回时间")
    private OffsetDateTime recallTime;

    @Schema(description = "创建时间")
    private OffsetDateTime createTime;
}
