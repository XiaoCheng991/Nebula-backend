package com.nebula.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 刷新Token请求DTO
 */
@Data
@Schema(description = "刷新Token请求")
public class RefreshTokenDTO {

    @NotBlank(message = "刷新令牌不能为空")
    @Schema(description = "刷新令牌", requiredMode = Schema.RequiredMode.REQUIRED)
    private String refreshToken;
}
