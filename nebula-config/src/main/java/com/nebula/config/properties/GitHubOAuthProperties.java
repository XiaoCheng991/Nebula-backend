package com.nebula.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * GitHub OAuth配置属性
 */
@Data
@Component
@ConfigurationProperties(prefix = "github.oauth")
public class GitHubOAuthProperties {

    /**
     * GitHub OAuth客户端ID
     */
    private String clientId;

    /**
     * GitHub OAuth客户端密钥
     */
    private String clientSecret;

    /**
     * GitHub OAuth授权回调地址
     */
    private String redirectUri;

    /**
     * 前端回调地址（用于登录成功后跳转）
     */
    private String frontendCallbackUrl;

    /**
     * 授权URL
     */
    private String authorizeUrl = "https://github.com/login/oauth/authorize";

    /**
     * 获取access_token的URL
     */
    private String tokenUrl = "https://github.com/login/oauth/access_token";

    /**
     * 获取用户信息的URL
     */
    private String userInfoUrl = "https://api.github.com/user";
}
