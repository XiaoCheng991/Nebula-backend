package com.nebula.model.entity.blog;

import com.baomidou.mybatisplus.annotation.*;
import com.nebula.model.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.OffsetDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("blog_article")
@Schema(description = "文章表")
public class BlogArticle extends BaseEntity {

    @TableId(type = IdType.AUTO)
    @Schema(description = "文章ID")
    private Long id;

    @Schema(description = "文章标题")
    private String title;

    @Schema(description = "文章别名")
    private String slug;

    @Schema(description = "文章摘要")
    private String summary;

    @Schema(description = "文章内容(Markdown)")
    private String content;

    @Schema(description = "文章内容(HTML)")
    private String contentHtml;

    @Schema(description = "封面图片")
    private String coverImage;

    @Schema(description = "分类ID")
    private Long categoryId;

    @Schema(description = "作者ID")
    private Long authorId;

    @Schema(description = "作者名称")
    private String authorName;

    @Schema(description = "浏览次数")
    private Long viewCount;

    @Schema(description = "点赞次数")
    private Long likeCount;

    @Schema(description = "评论次数")
    private Long commentCount;

    @Schema(description = "状态：DRAFT-草稿，PENDING-待审核，PUBLISHED-已发布，REJECTED-已拒绝")
    private String status;

    @Schema(description = "是否置顶")
    private Boolean isTop;

    @Schema(description = "是否推荐")
    private Boolean isRecommended;

    @Schema(description = "是否允许评论")
    private Boolean isCommentEnabled;

    @Schema(description = "字数")
    private Integer wordCount;

    @Schema(description = "发布时间")
    private OffsetDateTime publishTime;
}
