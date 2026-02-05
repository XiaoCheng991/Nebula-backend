package com.nebula.common.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

/**
 * JWT工具类
 */
public class JwtUtil {

    /**
     * 默认密钥（生产环境应从配置文件读取）
     */
    private static final String DEFAULT_SECRET = "nebula-secret-key-2024-spring-boot-jwt-token-refresh-enabled";

    /**
     * 生成JWT（使用默认过期时间）
     */
    public static String generateToken(Map<String, Object> claims) {
        return generateToken(claims, DEFAULT_SECRET, getDefaultExpiration());
    }

    /**
     * 生成Access Token（短期）
     */
    public static String generateAccessToken(Map<String, Object> claims, String secret, Long expiration) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        Date now = new Date();
        Date expireTime = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .claims(claims)
                .issuedAt(now)
                .expiration(expireTime)
                .signWith(key)
                .compact();
    }

    /**
     * 生成Refresh Token（长期）
     */
    public static String generateRefreshToken(Map<String, Object> claims, String secret, Long expiration) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        Date now = new Date();
        Date expireTime = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .claims(claims)
                .issuedAt(now)
                .expiration(expireTime)
                .signWith(key)
                .compact();
    }

    /**
     * 生成JWT（自定义密钥和过期时间）
     */
    public static String generateToken(Map<String, Object> claims, String secret, Long expiration) {
        return generateAccessToken(claims, secret, expiration);
    }

    /**
     * 解析JWT
     */
    public static Claims parseToken(String token) {
        return parseToken(token, DEFAULT_SECRET);
    }

    /**
     * 解析JWT
     */
    public static Claims parseToken(String token, String secret) {
        // 清理token：移除前后空格和 "Bearer " 前缀
        if (token == null || token.trim().isEmpty()) {
            throw new IllegalArgumentException("Token不能为空");
        }

        token = token.trim();

        // 移除 "Bearer " 前缀（如果有）
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // 再次trim，防止 "Bearer xxx" 这种情况
        token = token.trim();

        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 验证JWT
     */
    public static boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 验证JWT是否过期
     */
    public static boolean isTokenExpired(String token) {
        try {
            Claims claims = parseToken(token);
            Date expiration = claims.getExpiration();
            return expiration.before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * 获取默认过期时间（30分钟）
     */
    private static long getDefaultExpiration() {
        return 30 * 60 * 1000L;
    }
}
