package com.nebula.api.controller;

import com.nebula.common.exception.BusinessException;
import com.nebula.config.properties.GitHubOAuthProperties;
import com.nebula.config.result.Result;
import com.nebula.model.dto.GitHubOAuthConfirmDTO;
import com.nebula.model.dto.GitHubOAuthDTO;
import com.nebula.model.vo.GitHubOAuthConfirmVO;
import com.nebula.model.vo.LoginVO;
import com.nebula.service.service.OAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
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
    @Operation(summary = "GitHub OAuth回调 - 返回确认信息")
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

            // 获取确认信息（不直接登录）
            GitHubOAuthConfirmVO confirmVO = oAuthService.handleGitHubCallbackForConfirm(oauthDTO);

            // 重定向到前端确认页面，携带临时token
            String frontendUrl = String.format(
                "%s?tempToken=%s&githubId=%s",
                gitHubOAuthProperties.getFrontendCallbackUrl(),
                confirmVO.getTempToken(),
                confirmVO.getGithubId()
            );
            log.info("GitHub OAuth回调成功, 重定向到确认页面: {}", frontendUrl);
            response.sendRedirect(frontendUrl);

        } catch (BusinessException e) {
            log.error("GitHub OAuth业务异常: {}", e.getMessage(), e);
            response.sendRedirect(gitHubOAuthProperties.getFrontendCallbackUrl() + "?error=" + e.getCode());
        } catch (Exception e) {
            log.error("GitHub OAuth回调处理失败", e);
            response.sendRedirect(gitHubOAuthProperties.getFrontendCallbackUrl() + "?error=server_error");
        }
    }

    @GetMapping("/github/info")
    @Operation(summary = "用临时token获取GitHub用户信息")
    public Result<GitHubOAuthConfirmVO> getGitHubUserInfo(@RequestParam("tempToken") String tempToken) {
        log.info("获取GitHub用户信息请求, tempToken: {}", tempToken);
        GitHubOAuthConfirmVO confirmVO = oAuthService.getGitHubUserInfo(tempToken);
        return Result.success(confirmVO);
    }

    @PostMapping("/github/confirm")
    @Operation(summary = "确认GitHub OAuth登录")
    public Result<LoginVO> confirmGitHubLogin(@Valid @RequestBody GitHubOAuthConfirmDTO confirmDTO) {
        log.info("收到GitHub OAuth确认登录请求");
        LoginVO loginVO = oAuthService.confirmGitHubLogin(confirmDTO);
        return Result.success("登录成功", loginVO);
    }
}
