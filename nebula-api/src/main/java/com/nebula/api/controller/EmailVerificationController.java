package com.nebula.api.controller;

import com.nebula.common.util.JwtUtil;
import com.nebula.config.result.Result;
import com.nebula.model.dto.EmailVerificationDTO;
import com.nebula.model.dto.SendEmailVerificationDTO;
import com.nebula.model.dto.VerifyEmailDTO;
import com.nebula.service.service.EmailVerificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 邮箱验证控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/auth/email")
@RequiredArgsConstructor
@Tag(name = "邮箱验证", description = "邮箱验证相关接口")
public class EmailVerificationController {

    private final EmailVerificationService emailVerificationService;

    @PostMapping("/send-verification")
    @Operation(summary = "发送邮箱验证邮件", description = "向指定邮箱发送验证邮件")
    public Result<Void> sendVerificationEmail(@Valid @RequestBody EmailVerificationDTO verificationDTO) {
        emailVerificationService.sendVerificationEmail(verificationDTO);
        // 为了安全，始终返回成功
        return Result.success("如果该邮箱存在，我们将发送验证邮件");
    }

    @PostMapping("/send")
    @Operation(summary = "发送验证邮件(简化版)", description = "发送邮箱验证邮件")
    public Result<Void> sendEmailVerification(@Valid @RequestBody SendEmailVerificationDTO dto) {
        EmailVerificationDTO verificationDTO = new EmailVerificationDTO();
        verificationDTO.setEmail(dto.getEmail());
        verificationDTO.setType(dto.getType());
        verificationDTO.setCaptcha(dto.getCaptcha());
        verificationDTO.setCaptchaKey(dto.getCaptchaKey());

        emailVerificationService.sendVerificationEmail(verificationDTO);
        return Result.success("验证邮件已发送");
    }

    @PostMapping("/verify")
    @Operation(summary = "验证邮箱", description = "验证邮箱验证码")
    public Result<Void> verifyEmail(@Valid @RequestBody VerifyEmailDTO verifyDTO) {
        boolean success = emailVerificationService.verifyEmail(verifyDTO);
        if (success) {
            return Result.success("邮箱验证成功");
        } else {
            return Result.error("邮箱验证失败");
        }
    }

    @GetMapping("/status")
    @Operation(summary = "检查邮箱验证状态", description = "检查当前用户的邮箱是否已验证",
            security = @SecurityRequirement(name = "Authorization"))
    public Result<Boolean> checkEmailStatus(HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return Result.error("请先登录");
        }
        boolean verified = emailVerificationService.isEmailVerified(userId);
        return Result.success(verified);
    }

    @PostMapping("/resend")
    @Operation(summary = "重新发送验证邮件", description = "重新发送邮箱验证邮件",
            security = @SecurityRequirement(name = "Authorization"))
    public Result<Void> resendVerificationEmail(HttpServletRequest request) {
        // 从请求中获取用户邮箱
        // 这里简化处理，实际应该从用户信息中获取邮箱
        String email = getCurrentUserEmail(request);
        if (email == null) {
            return Result.error("请先登录");
        }

        emailVerificationService.resendVerificationEmail(email);
        return Result.success("验证邮件已重新发送");
    }

    /**
     * 从请求中获取当前用户ID
     */
    private Long getCurrentUserId(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            return null;
        }
        token = token.substring(7);
        try {
            return JwtUtil.getUserIdFromToken(token);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 从请求中获取当前用户邮箱
     */
    private String getCurrentUserEmail(HttpServletRequest request) {
        // 简化处理，实际应该从数据库查询用户信息
        // 这里仅作示例
        return null;
    }
}
