package com.nebula.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.OffsetDateTime;

/**
 * 博客文章VO
 */
@Data
@Schema(description = "博客文章")
public class BlogArticleVO {

    @Schema(description = "文章ID")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "头像")
    private String avatar;

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

    @Schema(description = "分类名称")
    private String categoryName;

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

    @Schema(description = "心情（happy, sad, excited, calm, anxious等）")
    private String mood;

    @Schema(description = "位置")
    private String location;

    @Schema(description = "天气")
    private String weather;

    @Schema(description = "是否已点赞")
    private Boolean isLiked;

    @Schema(description = "创建时间")
    private OffsetDateTime createTime;

    @Schema(description = "更新时间")
    private OffsetDateTime updateTime;

    @Schema(description = "标签列表")
    private String tags;

    @Schema(description = "评论列表")
    private String comments;

}
