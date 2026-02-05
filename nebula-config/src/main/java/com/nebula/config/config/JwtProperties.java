package com.nebula.config.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT配置属性
 */
@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /**
     * JWT密钥
     */
    private String secret = "nebula-secret-key-2024-spring-boot-jwt-token-refresh-enabled";

    /**
     * Access Token过期时间（毫秒），默认30分钟
     */
    private Long accessTokenExpiration = 30 * 60 * 1000L;

    /**
     * Refresh Token过期时间（毫秒），默认7天
     */
    private Long refreshTokenExpiration = 7 * 24 * 60 * 60 * 1000L;
}
