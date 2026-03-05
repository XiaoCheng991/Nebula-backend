package com.nebula.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * GitHub OAuth确认信息VO
 * 用于前端显示用户确认界面
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "GitHub OAuth确认信息")
public class GitHubOAuthConfirmVO {

    @Schema(description = "临时令牌，用于确认登录")
    private String tempToken;

    @Schema(description = "GitHub用户ID")
    private Long githubId;

    @Schema(description = "GitHub用户名")
    private String githubLogin;

    @Schema(description = "建议的用户名")
    private String username;

    @Schema(description = "建议的昵称")
    private String nickname;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "头像URL")
    private String avatarUrl;

    @Schema(description = "个人简介")
    private String bio;

    @Schema(description = "是否是新用户")
    private Boolean isNewUser;
}
