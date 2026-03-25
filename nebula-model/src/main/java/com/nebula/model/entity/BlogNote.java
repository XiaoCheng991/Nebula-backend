package com.nebula.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.OffsetDateTime;

/**
 * 日常碎碎念实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("blog_note")
@Schema(description = "日常碎碎念")
public class BlogNote extends BaseEntity {

    @TableId(type = IdType.AUTO)
    @Schema(description = "笔记ID")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "内容")
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

    @Schema(description = "点赞次数")
    private Long likeCount;

    @Schema(description = "评论次数")
    private Long commentCount;

    @Schema(description = "创建时间")
    private OffsetDateTime createTime;

    @Schema(description = "更新时间")
    private OffsetDateTime updateTime;

    @TableLogic
    @Schema(description = "逻辑删除标志")
    private Integer deleted;
}
