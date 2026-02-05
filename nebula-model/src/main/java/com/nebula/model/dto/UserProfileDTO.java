package com.nebula.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户档案DTO
 */
@Data
@Schema(description = "用户档案")
public class UserProfileDTO {

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "显示名称")
    private String displayName;

    @Schema(description = "头像URL")
    private String avatar;

    @Schema(description = "个人简介")
    private String bio;
}
