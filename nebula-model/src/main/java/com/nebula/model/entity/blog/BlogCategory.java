package com.nebula.model.entity.blog;

import com.baomidou.mybatisplus.annotation.*;
import com.nebula.model.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("blog_category")
@Schema(description = "文章分类表")
public class BlogCategory extends BaseEntity {

    @TableId(type = IdType.AUTO)
    @Schema(description = "分类ID")
    private Long id;

    @Schema(description = "父分类ID")
    private Long parentId;

    @Schema(description = "分类名称")
    private String categoryName;

    @Schema(description = "分类别名")
    private String slug;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "图标")
    private String icon;

    @Schema(description = "排序")
    private Integer sortOrder;

    @Schema(description = "文章数量")
    private Integer articleCount;

    @Schema(description = "状态：ACTIVE-启用，DISABLED-禁用")
    private String status;

    @Schema(description = "创建者ID")
    private Long createBy;
}
