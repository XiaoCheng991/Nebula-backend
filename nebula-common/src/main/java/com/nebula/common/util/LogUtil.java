package com.nebula.common.util;

import lombok.experimental.UtilityClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.nebula.common.util.LogUtil.Utils.maskToken;

/**
 * 统一日志工具类
 * 提供标准化的日志格式和常用日志记录方法
 */
@UtilityClass
public class LogUtil {

    /**
     * 获取 Logger 实例
     */
    public static Logger getLogger(Class<?> clazz) {
        return LoggerFactory.getLogger(clazz);
    }

    /**
     * 获取 Logger 实例（字符串名称）
     */
    public static Logger getLogger(String name) {
        return LoggerFactory.getLogger(name);
    }

    /**
     * 日志模板格式化
     */
    public static String format(String template, Object... args) {
        if (args == null || args.length == 0) {
            return template;
        }
        return String.format(template.replace("{}", "%s"), args);
    }

    /**
     * 认证相关日志
     */
    @UtilityClass
    public static class Auth {

        /**
         * 登录成功日志
         */
        public static void loginSuccess(Logger logger, Long userId, String email) {
            logger.info("用户登录成功 | userId={}, email={}", userId, email);
        }

        /**
         * 登录失败日志
         */
        public static void loginFailed(Logger logger, String email, String reason) {
            logger.warn("用户登录失败 | email={}, reason={}", email, reason);
        }

        /**
         * 注册成功日志
         */
        public static void registerSuccess(Logger logger, Long userId, String email, String username) {
            logger.info("用户注册成功 | userId={}, email={}, username={}", userId, email, username);
        }

        /**
         * 注册失败日志
         */
        public static void registerFailed(Logger logger, String email, String reason) {
            logger.warn("用户注册失败 | email={}, reason={}", email, reason);
        }

        /**
         * 登出日志
         */
        public static void logout(Logger logger, Long userId) {
            logger.info("用户登出 | userId={}", userId);
        }

        /**
         * Token 刷新成功日志
         */
        public static void tokenRefreshSuccess(Logger logger, Long userId) {
            logger.info("刷新Token成功 | userId={}", userId);
        }

        /**
         * Token 刷新失败日志
         */
        public static void tokenRefreshFailed(Logger logger, String reason) {
            logger.warn("刷新Token失败 | reason={}", reason);
        }

        /**
         * Token 验证失败日志
         */
        public static void tokenValidateFailed(Logger logger, String token, String reason) {
            logger.warn("Token验证失败 | token={}, reason={}", maskToken(token), reason);
        }

        /**
         * 获取用户信息日志
         */
        public static void getUserInfo(Logger logger, Long userId) {
            logger.debug("获取用户信息 | userId={}", userId);
        }
    }

    /**
     * Redis 操作日志
     */
    @UtilityClass
    public static class Redis {

        /**
         * 缓存设置日志
         */
        public static void set(Logger logger, String key, long ttl) {
            logger.debug("Redis设置 | key={}, ttl={}s", key, ttl);
        }

        /**
         * 缓存获取日志
         */
        public static void get(Logger logger, String key, boolean hit) {
            logger.debug("Redis获取 | key={}, hit={}", key, hit ? "命中" : "未命中");
        }

        /**
         * 缓存删除日志
         */
        public static void delete(Logger logger, String key) {
            logger.debug("Redis删除 | key={}", key);
        }

        /**
         * 缓存操作失败日志
         */
        public static void error(Logger logger, String operation, String key, String reason) {
            logger.error("Redis操作失败 | operation={}, key={}, reason={}", operation, key, reason);
        }
    }

    /**
     * API 请求日志
     */
    @UtilityClass
    public static class Api {

        /**
         * 请求开始日志
         */
        public static void requestStart(Logger logger, String method, String uri) {
            logger.info("API请求 | method={}, uri={}", method, uri);
        }

        /**
         * 请求成功日志
         */
        public static void requestSuccess(Logger logger, String method, String uri, long duration) {
            logger.info("API成功 | method={}, uri={}, duration={}ms", method, uri, duration);
        }

        /**
         * 请求失败日志
         */
        public static void requestFailed(Logger logger, String method, String uri, String error) {
            logger.error("API失败 | method={}, uri={}, error={}", method, uri, error);
        }
    }

    /**
     * 数据库操作日志
     */
    @UtilityClass
    public static class Database {

        /**
         * 查询日志
         */
        public static void query(Logger logger, String table, Object id) {
            logger.debug("数据库查询 | table={}, id={}", table, id);
        }

        /**
         * 插入日志
         */
        public static void insert(Logger logger, String table, Object id) {
            logger.info("数据库插入 | table={}, id={}", table, id);
        }

        /**
         * 更新日志
         */
        public static void update(Logger logger, String table, Object id) {
            logger.info("数据库更新 | table={}, id={}", table, id);
        }

        /**
         * 删除日志
         */
        public static void delete(Logger logger, String table, Object id) {
            logger.info("数据库删除 | table={}, id={}", table, id);
        }

        /**
         * 操作失败日志
         */
        public static void error(Logger logger, String operation, String table, String reason) {
            logger.error("数据库操作失败 | operation={}, table={}, reason={}", operation, table, reason);
        }
    }

    /**
     * 工具方法
     */
    @UtilityClass
    public static class Utils {

        /**
         * 隐藏 Token（只显示前 6 位和后 4 位）
         */
        public static String maskToken(String token) {
            if (token == null || token.length() < 10) {
                return "***";
            }
            return token.substring(0, 6) + "..." + token.substring(token.length() - 4);
        }

        /**
         * 隐藏邮箱（只显示前 2 位和域名）
         */
        public static String maskEmail(String email) {
            if (email == null || !email.contains("@")) {
                return "***";
            }
            int atIndex = email.indexOf("@");
            String prefix = email.substring(0, Math.min(2, atIndex));
            return prefix + "***" + email.substring(atIndex);
        }

        /**
         * 格式化耗时
         */
        public static String formatDuration(long ms) {
            if (ms < 1000) {
                return ms + "ms";
            } else if (ms < 60000) {
                return String.format("%.2fs", ms / 1000.0);
            } else {
                long minutes = ms / 60000;
                long seconds = (ms % 60000) / 1000;
                return String.format("%dm%ds", minutes, seconds);
            }
        }
    }
}
