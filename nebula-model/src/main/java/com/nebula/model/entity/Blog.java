package com.nebula.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.OffsetDateTime;

/**
 * 博客实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("blog")
@Schema(description = "博客")
public class Blog extends BaseEntity {

    @TableId(type = IdType.AUTO)
    @Schema(description = "博客ID")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "内容")
    private String content;

    @Schema(description = "摘要")
    private String summary;

    @Schema(description = "封面图URL")
    private String coverUrl;

    @Schema(description = "状态（0-草稿，1-已发布，2-已删除）")
    private Integer status;

    @Schema(description = "是否置顶（0-否，1-是）")
    private Integer isTop;

    @Schema(description = "浏览次数")
    private Integer viewCount;

    @Schema(description = "点赞次数")
    private Integer likeCount;

    @Schema(description = "评论次数")
    private Integer commentCount;

    @Schema(description = "标签")
    private String tags;

    @Schema(description = "分类")
    private String category;

    @Schema(description = "发布时间")
    private OffsetDateTime publishTime;

    @Schema(description = "创建时间")
    private OffsetDateTime createTime;

    @Schema(description = "更新时间")
    private OffsetDateTime updateTime;
}
