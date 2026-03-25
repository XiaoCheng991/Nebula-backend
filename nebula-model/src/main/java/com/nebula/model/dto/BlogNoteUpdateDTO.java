package com.nebula.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 更新日常碎碎念DTO
 */
@Data
@Schema(description = "更新日常碎碎念请求")
public class BlogNoteUpdateDTO {

    @Schema(description = "笔记ID")
    @NotNull(message = "笔记ID不能为空")
    private Long id;

    @Schema(description = "内容")
    @NotBlank(message = "内容不能为空")
    private String content;

    @Schema(description = "心情")
    private String mood;

    @Schema(description = "标签")
    private String tags;

    @Schema(description = "位置")
    private String location;

    @Schema(description = "天气")
    private String weather;

    @Schema(description = "是否公开")
    private Boolean isPublic;
}
