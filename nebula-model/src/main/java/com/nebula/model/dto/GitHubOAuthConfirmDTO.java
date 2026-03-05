package com.nebula.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * GitHub OAuth确认登录DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "GitHub OAuth确认登录请求")
public class GitHubOAuthConfirmDTO {

    @NotBlank(message = "临时令牌不能为空")
    @Schema(description = "临时令牌", required = true)
    private String tempToken;

    @Schema(description = "确认的用户名（可选，不填则使用建议值）")
    private String username;

    @Schema(description = "确认的昵称（可选，不填则使用建议值）")
    private String nickname;

    @Schema(description = "确认的邮箱（可选，不填则使用建议值）")
    private String email;
}
