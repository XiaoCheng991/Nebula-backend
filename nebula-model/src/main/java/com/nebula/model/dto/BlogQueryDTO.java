package com.nebula.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 博客查询DTO
 */
@Data
@Schema(description = "博客查询请求")
public class BlogQueryDTO {

    @Schema(description = "关键词（搜索标题和内容）")
    private String keyword;

    @Schema(description = "标签")
    private String tag;

    @Schema(description = "分类")
    private String category;

    @Schema(description = "状态（0-草稿，1-已发布，2-已删除）")
    private Integer status;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "是否只看置顶")
    private Integer isTop;

    @Schema(description = "排序字段（createTime, viewCount, likeCount）")
    private String sortField;

    @Schema(description = "排序方式（asc, desc）")
    private String sortOrder;

    @Schema(description = "页码")
    private Integer pageNum = 1;

    @Schema(description = "每页数量")
    private Integer pageSize = 10;
}
