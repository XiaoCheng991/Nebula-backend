package com.nebula.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * GitHub用户信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "GitHub用户信息")
public class GitHubUserInfo {

    @Schema(description = "GitHub用户ID")
    private Long id;

    @Schema(description = "用户名")
    private String login;

    @Schema(description = "显示名称")
    private String name;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "头像URL")
    private String avatarUrl;

    @Schema(description = "个人简介")
    private String bio;
}
