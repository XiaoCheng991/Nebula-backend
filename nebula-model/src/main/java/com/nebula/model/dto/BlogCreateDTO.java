package com.nebula.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 创建博客DTO
 */
@Data
@Schema(description = "创建博客请求")
public class BlogCreateDTO {

    @Schema(description = "标题")
    @NotBlank(message = "标题不能为空")
    @Size(max = 200, message = "标题长度不能超过200个字符")
    private String title;

    @Schema(description = "内容")
    @NotBlank(message = "内容不能为空")
    private String content;

    @Schema(description = "摘要")
    @Size(max = 500, message = "摘要长度不能超过500个字符")
    private String summary;

    @Schema(description = "封面图URL")
    private String coverUrl;

    @Schema(description = "状态（0-草稿，1-已发布）")
    private Integer status;

    @Schema(description = "标签（多个标签用逗号分隔）")
    private String tags;

    @Schema(description = "分类")
    private String category;
}
