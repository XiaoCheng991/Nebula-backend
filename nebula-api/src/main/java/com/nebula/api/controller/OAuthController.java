package com.nebula.api.controller;

import com.nebula.common.exception.BusinessException;
import com.nebula.config.properties.GitHubOAuthProperties;
import com.nebula.config.result.Result;
import com.nebula.model.dto.GitHubOAuthDTO;
import com.nebula.model.vo.LoginVO;
import com.nebula.service.service.OAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * OAuth控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/oauth")
@RequiredArgsConstructor
@Tag(name = "OAuth管理", description = "GitHub第三方登录相关接口")
public class OAuthController {

    private static final String GITHUB_STATE_PREFIX = "github:oauth:state:";
    private static final long STATE_EXPIRE_SECONDS = 300; // 5分钟过期

    private final OAuthService oAuthService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final GitHubOAuthProperties gitHubOAuthProperties;

    @GetMapping("/github/authorize")
    @Operation(summary = "获取GitHub授权URL")
    public Result<Map<String, String>> getGitHubAuthorizeUrl() {
        // 生成随机 state 并存储到 Redis
        String state = UUID.randomUUID().toString();
        String stateKey = GITHUB_STATE_PREFIX + state;
        redisTemplate.opsForValue().set(stateKey, "pending", STATE_EXPIRE_SECONDS, java.util.concurrent.TimeUnit.SECONDS);

        log.info("生成GitHub OAuth state: {}, 有效期: {}秒", state, STATE_EXPIRE_SECONDS);

        String authorizeUrl = oAuthService.getGitHubAuthorizeUrl(state);

        Map<String, String> data = new HashMap<>();
        data.put("authorizeUrl", authorizeUrl);
        data.put("state", state);
        return Result.success(data);
    }

    @GetMapping("/github/callback")
    @Operation(summary = "GitHub OAuth回调")
    public void handleGitHubCallback(
            @RequestParam("code") String code,
            @RequestParam(value = "state", required = false) String state,
            HttpServletResponse response) throws IOException {

        log.info("收到GitHub OAuth回调");

        try {
            // 如果有 state 参数，验证一下
            if (state != null && !state.isEmpty()) {
                String stateKey = GITHUB_STATE_PREFIX + state;
                String stateValue = (String) redisTemplate.opsForValue().get(stateKey);
                if (stateValue == null) {
                    log.error("GitHub OAuth state验证失败: {}", state);
                    response.sendRedirect(gitHubOAuthProperties.getFrontendCallbackUrl() + "?error=invalid_state");
                    return;
                }
                // 验证通过后删除state，防止重复使用
                redisTemplate.delete(stateKey);
                log.debug("GitHub OAuth state验证通过: {}", state);
            }

            GitHubOAuthDTO oauthDTO = new GitHubOAuthDTO();
            oauthDTO.setCode(code);
            oauthDTO.setState(state);

            LoginVO loginVO = oAuthService.handleGitHubCallback(oauthDTO);

            // 重定向到前端，携带 accessToken 和 refreshToken
            String frontendUrl = String.format(
                "%s?token=%s&refreshToken=%s",
                gitHubOAuthProperties.getFrontendCallbackUrl(),
                loginVO.getToken(),
                loginVO.getRefreshToken()
            );
            log.info("GitHub OAuth登录成功, 重定向到: {}", frontendUrl);
            response.sendRedirect(frontendUrl);

        } catch (BusinessException e) {
            log.error("GitHub OAuth业务异常: {}", e.getMessage(), e);
            response.sendRedirect(gitHubOAuthProperties.getFrontendCallbackUrl() + "?error=" + e.getCode());
        } catch (Exception e) {
            log.error("GitHub OAuth回调处理失败", e);
            response.sendRedirect(gitHubOAuthProperties.getFrontendCallbackUrl() + "?error=server_error");
        }
    }
}
