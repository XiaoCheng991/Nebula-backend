package com.nebula.api.controller;

import com.nebula.config.result.Result;
import com.nebula.model.dto.GitHubOAuthDTO;
import com.nebula.model.vo.LoginVO;
import com.nebula.service.service.OAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * OAuth控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/oauth")
@RequiredArgsConstructor
@Tag(name = "OAuth管理", description = "GitHub第三方登录相关接口")
public class OAuthController {

    private final OAuthService oAuthService;

    @GetMapping("/github/authorize")
    @Operation(summary = "获取GitHub授权URL")
    public Result<Map<String, String>> getGitHubAuthorizeUrl() {
        String authorizeUrl = oAuthService.getGitHubAuthorizeUrl();
        Map<String, String> data = new HashMap<>();
        data.put("authorizeUrl", authorizeUrl);
        return Result.success(data);
    }

    @GetMapping("/github/callback")
    @Operation(summary = "GitHub OAuth回调")
    public void handleGitHubCallback(
            @RequestParam("code") String code,
            @RequestParam(required = false) String state,
            HttpServletResponse response) throws IOException {
        try {
            GitHubOAuthDTO oauthDTO = new GitHubOAuthDTO();
            oauthDTO.setCode(code);
            oauthDTO.setState(state);

            LoginVO loginVO = oAuthService.handleGitHubCallback(oauthDTO);

            // 重定向到前端，携带token
            String frontendUrl = "http://localhost:3000/auth/github/callback?token=" + loginVO.getToken();
            response.sendRedirect(frontendUrl);
        } catch (Exception e) {
            log.error("GitHub OAuth回调处理失败", e);
            response.sendRedirect("http://localhost:3000/login?error=github_auth_failed");
        }
    }
}
