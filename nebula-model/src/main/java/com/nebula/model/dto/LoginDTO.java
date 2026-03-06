package com.nebula.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 登录DTO
 */
@Data
@Schema(description = "登录请求")
public class LoginDTO {

    @Schema(description = "用户名或邮箱")
    @NotBlank(message = "用户名或邮箱不能为空")
    private String account;

    @Schema(description = "密码")
    @NotBlank(message = "密码不能为空")
    private String password;
}
