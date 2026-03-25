package com.nebula.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 创建日常碎碎念DTO
 */
@Data
@Schema(description = "创建日常碎碎念请求")
public class BlogNoteCreateDTO {

    @Schema(description = "内容")
    @NotBlank(message = "内容不能为空")
    private String content;

    @Schema(description = "心情（happy, sad, excited, calm, anxious等）")
    private String mood;

    @Schema(description = "标签（多个标签用逗号分隔）")
    private String tags;

    @Schema(description = "位置")
    private String location;

    @Schema(description = "天气")
    private String weather;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "是否公开")
    private Boolean isPublic = true;
}
