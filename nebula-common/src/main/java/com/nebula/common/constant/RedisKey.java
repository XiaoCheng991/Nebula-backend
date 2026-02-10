package com.nebula.common.constant;

import java.util.concurrent.TimeUnit;

/**
 * Redis Key 统一管理
 * 集中管理所有 Redis Key 的生成、解析和常量定义
 */
public class RedisKey {

    private static final String PREFIX = "nebula";
    private static final String DELIMITER = ":";

    /**
     * Token 相关 Key
     */
    public static class Token {

        private static final String TOKEN = "token";
        private static final String ACCESS = "access";
        private static final String REFRESH = "refresh";

        /**
         * 生成用户 Access Token 存储的 Key（按用户索引）
         * 格式: nebula:token:access:user:{userId}
         */
        public static String accessTokenByUser(Long userId) {
            return join(PREFIX, TOKEN, ACCESS, "user", String.valueOf(userId));
        }

        /**
         * 生成 Access Token 对应用户的 Key（按 Token 索引）
         * 格式: nebula:token:access:value:{accessToken}
         */
        public static String userByAccessToken(String accessToken) {
            return join(PREFIX, TOKEN, ACCESS, "value", accessToken);
        }

        /**
         * 生成用户 Refresh Token 存储的 Key（按用户索引）
         * 格式: nebula:token:refresh:user:{userId}
         */
        public static String refreshTokenByUser(Long userId) {
            return join(PREFIX, TOKEN, REFRESH, "user", String.valueOf(userId));
        }

        /**
         * 生成 Refresh Token 对应用户的 Key（按 Token 索引）
         * 格式: nebula:token:refresh:value:{refreshToken}
         */
        public static String userByRefreshToken(String refreshToken) {
            return join(PREFIX, TOKEN, REFRESH, "value", refreshToken);
        }

        /**
         * Access Token 默认过期时间（30分钟）
         */
        public static final long ACCESS_TOKEN_TTL = 30 * 60;

        /**
         * Refresh Token 默认过期时间（7天）
         */
        public static final long REFRESH_TOKEN_TTL = 7 * 24 * 60 * 60;

        /**
         * 获取 Access Token 过期时间（秒）
         */
        public static long getAccessTokenTtlSeconds(long expirationMs) {
            return expirationMs / 1000;
        }

        /**
         * 获取 Refresh Token 过期时间（秒）
         */
        public static long getRefreshTokenTtlSeconds(long expirationMs) {
            return expirationMs / 1000;
        }
    }

    /**
     * 用户相关 Key
     */
    public static class User {

        private static final String USER = "user";

        /**
         * 用户基本信息缓存
         * 格式: nebula:user:info:{userId}
         */
        public static String userInfo(Long userId) {
            return join(PREFIX, USER, "info", String.valueOf(userId));
        }

        /**
         * 用户信息缓存过期时间（1小时）
         */
        public static final long INFO_TTL = 60 * 60;

        /**
         * 用户在线状态
         * 格式: nebula:user:online:{userId}
         */
        public static String onlineStatus(Long userId) {
            return join(PREFIX, USER, "online", String.valueOf(userId));
        }

        /**
         * 用户会话列表
         * 格式: nebula:user:sessions:{userId}
         */
        public static String sessions(Long userId) {
            return join(PREFIX, USER, "sessions", String.valueOf(userId));
        }
    }

    /**
     * 限流相关 Key
     */
    public static class RateLimit {

        private static final String RATE_LIMIT = "ratelimit";

        /**
         * 接口限流
         * 格式: nebula:ratelimit:api:{endpoint}:{ipOrUserId}
         */
        public static String api(String endpoint, String identifier) {
            return join(PREFIX, RATE_LIMIT, "api", endpoint, identifier);
        }

        /**
         * 登录限流
         * 格式: nebula:ratelimit:login:{ipOrEmail}
         */
        public static String login(String identifier) {
            return join(PREFIX, RATE_LIMIT, "login", identifier);
        }
    }

    /**
     * 验证码相关 Key
     */
    public static class Captcha {

        private static final String CAPTCHA = "captcha";

        /**
         * 邮箱验证码
         * 格式: nebula:captcha:email:{email}
         */
        public static String email(String email) {
            return join(PREFIX, CAPTCHA, "email", email);
        }

        /**
         * 验证码过期时间（5分钟）
         */
        public static final long TTL = 5 * 60;
    }

    /**
     * 拼接 Redis Key
     */
    private static String join(String... parts) {
        return String.join(DELIMITER, parts);
    }

    /**
     * 时间单位工具方法
     */
    public static class TimeUtils {

        /**
         * 分钟转秒
         */
        public static long minutesToSeconds(long minutes) {
            return minutes * 60;
        }

        /**
         * 小时转秒
         */
        public static long hoursToSeconds(long hours) {
            return hours * 60 * 60;
        }

        /**
         * 天转秒
         */
        public static long daysToSeconds(long days) {
            return days * 24 * 60 * 60;
        }
    }
}
