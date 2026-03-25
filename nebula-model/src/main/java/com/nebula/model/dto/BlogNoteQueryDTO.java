package com.nebula.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 日常碎碎念查询DTO
 */
@Data
@Schema(description = "日常碎碎念查询请求")
public class BlogNoteQueryDTO {

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "心情")
    private String mood;

    @Schema(description = "标签")
    private String tag;

    @Schema(description = "关键词")
    private String keyword;

    @Schema(description = "是否公开")
    private Boolean isPublic;

    @Schema(description = "排序字段（createTime, likeCount）")
    private String sortField = "createTime";

    @Schema(description = "排序方式（asc, desc）")
    private String sortOrder = "desc";

    @Schema(description = "页码")
    private Integer pageNum = 1;

    @Schema(description = "每页数量")
    private Integer pageSize = 20;
}
