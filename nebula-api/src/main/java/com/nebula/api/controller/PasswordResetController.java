package com.nebula.api.controller;

import com.nebula.config.result.Result;
import com.nebula.model.dto.PasswordResetConfirmDTO;
import com.nebula.model.dto.PasswordResetRequestDTO;
import com.nebula.service.service.PasswordResetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 密码重置控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/auth/password")
@RequiredArgsConstructor
@Tag(name = "密码重置", description = "密码重置相关接口")
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    @PostMapping("/forgot")
    @Operation(summary = "请求密码重置", description = "发送密码重置邮件到用户邮箱")
    public Result<Void> requestPasswordReset(@Valid @RequestBody PasswordResetRequestDTO requestDTO) {
        passwordResetService.requestPasswordReset(requestDTO);
        // 为了安全，始终返回成功，不暴露邮箱是否存在
        return Result.success("如果该邮箱存在，我们将发送密码重置邮件");
    }

    @GetMapping("/validate-token")
    @Operation(summary = "验证重置令牌", description = "检查密码重置令牌是否有效")
    @Parameter(name = "token", description = "重置令牌", required = true)
    public Result<Boolean> validateToken(@RequestParam String token) {
        boolean valid = passwordResetService.validateToken(token);
        return Result.success(valid);
    }

    @PostMapping("/reset")
    @Operation(summary = "确认密码重置", description = "使用令牌重置密码")
    public Result<Void> confirmPasswordReset(@Valid @RequestBody PasswordResetConfirmDTO confirmDTO) {
        passwordResetService.confirmPasswordReset(confirmDTO);
        return Result.success("密码重置成功");
    }
}
