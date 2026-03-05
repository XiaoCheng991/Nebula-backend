package com.nebula.config.util;

import com.nebula.common.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 安全上下文工具类 - 仅负责从请求中获取token和用户ID
 */
@Slf4j
public class SecurityContext {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    /**
     * 从请求中获取当前用户ID
     */
    public static Long getCurrentUserId(String jwtSecret) {
        String token = getTokenFromRequest();
        if (token == null) {
            return null;
        }
        try {
            return JwtUtil.getUserIdFromToken(token, jwtSecret);
        } catch (Exception e) {
            log.warn("解析Token获取用户ID失败", e);
            return null;
        }
    }

    /**
     * 从请求中获取token
     */
    public static String getTokenFromRequest() {
        HttpServletRequest request = getRequest();
        if (request == null) {
            return null;
        }
        String authHeader = request.getHeader(AUTHORIZATION_HEADER);
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            return null;
        }
        return authHeader.substring(BEARER_PREFIX.length());
    }

    /**
     * 获取当前请求
     */
    private static HttpServletRequest getRequest() {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }
}
