package com.nebula.model.entity.blog;

import com.baomidou.mybatisplus.annotation.*;
import com.nebula.model.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("blog_comment")
@Schema(description = "评论表")
public class BlogComment extends BaseEntity {

    @TableId(type = IdType.AUTO)
    @Schema(description = "评论ID")
    private Long id;

    @Schema(description = "文章ID")
    private Long articleId;

    @Schema(description = "父评论ID")
    private Long parentId;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "头像URL")
    private String avatarUrl;

    @Schema(description = "评论内容")
    private String content;

    @Schema(description = "IP地址")
    private String ipAddress;

    @Schema(description = "地理位置")
    private String location;

    @Schema(description = "点赞次数")
    private Long likeCount;

    @Schema(description = "状态：PENDING-待审核，APPROVED-已通过，REJECTED-已拒绝")
    private String status;
}
