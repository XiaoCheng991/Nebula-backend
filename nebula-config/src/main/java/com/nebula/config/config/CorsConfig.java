package com.nebula.config.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * 跨域配置
 * 解决前端（localhost:3000）访问后端（localhost:8080）的跨域问题
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // 允许所有源（开发环境）
        // 生产环境应该指定具体的前端域名，如：http://www.example.com
        config.addAllowedOriginPattern("*");

        // 允许携带认证信息（Cookie、Authorization header等）
        config.setAllowCredentials(true);

        // 允许所有请求头
        config.addAllowedHeader("*");

        // 允许所有请求方法（GET、POST、PUT、DELETE等）
        config.addAllowedMethod("*");

        // 预检请求的有效期（秒）
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}
