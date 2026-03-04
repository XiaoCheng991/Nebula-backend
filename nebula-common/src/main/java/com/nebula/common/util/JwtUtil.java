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
     * 从Token中获取用户ID
     */
    public static Long getUserIdFromToken(String token, String secret) {
        Claims claims = parseToken(token, secret);
        Object userId = claims.get("userId");
        if (userId == null) {
            throw new IllegalArgumentException("Token中不包含用户ID");
        }
        if (userId instanceof Number) {
            return ((Number) userId).longValue();
        }
        return Long.parseLong(userId.toString());
    }
}
