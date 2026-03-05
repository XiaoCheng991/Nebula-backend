package com.nebula.model.entity.im;

import com.baomidou.mybatisplus.annotation.*;
import com.nebula.model.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("im_sensitive_word")
@Schema(description = "敏感词表")
public class ImSensitiveWord extends BaseEntity {

    @TableId(type = IdType.AUTO)
    @Schema(description = "敏感词ID")
    private Long id;

    @Schema(description = "敏感词")
    private String word;

    @Schema(description = "敏感词类型：NORMAL-普通，POLITICAL-政治，PORN-色情，VIOLENCE-暴力")
    private String wordType;

    @Schema(description = "替换字符")
    private String replaceStr;

    @Schema(description = "是否启用")
    private Boolean isEnabled;

    @Schema(description = "创建者ID")
    private Long createBy;
}
