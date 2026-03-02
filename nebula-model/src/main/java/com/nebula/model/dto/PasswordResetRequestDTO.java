package com.nebula.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 密码重置请求DTO
 */
@Data
@Schema(description = "密码重置请求")
public class PasswordResetRequestDTO {

    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    @Schema(description = "邮箱地址", required = true)
    private String email;

    @Schema(description = "验证码（图形验证码）")
    private String captcha;

    @Schema(description = "验证码Key")
    private String captchaKey;
}
