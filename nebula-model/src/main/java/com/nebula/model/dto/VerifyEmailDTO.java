package com.nebula.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 验证邮箱DTO
 */
@Data
@Schema(description = "验证邮箱确认")
public class VerifyEmailDTO {

    @NotBlank(message = "验证令牌不能为空")
    @Schema(description = "验证令牌", required = true)
    private String token;
}
