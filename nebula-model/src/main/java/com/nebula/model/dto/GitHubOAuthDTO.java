package com.nebula.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * GitHub OAuth回调DTO
 */
@Data
@Schema(description = "GitHub OAuth回调")
public class GitHubOAuthDTO {

    @Schema(description = "GitHub授权码")
    private String code;

    @Schema(description = "GitHub状态码")
    private String state;
}
