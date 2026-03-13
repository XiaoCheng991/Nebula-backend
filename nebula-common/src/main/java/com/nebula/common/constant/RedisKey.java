package com.nebula.common.constant;

/**
 * Redis Key 统一管理
 * 集中管理所有 Redis Key 的生成、解析和常量定义
 */
public class RedisKey {

    private static final String PREFIX = "nebula";
    private static final String DELIMITER = ":";

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
