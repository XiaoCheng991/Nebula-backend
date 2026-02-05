package com.nebula.config.constant;

/**
 * Redis常量
 */
public class RedisConstant {

    /**
     * Token缓存前缀
     */
    public static final String TOKEN_PREFIX = "nebula:token:";

    /**
     * 用户缓存前缀
     */
    public static final String USER_PREFIX = "nebula:user:";

    /**
     * Token过期时间（秒）
     */
    public static final long TOKEN_EXPIRE = 7 * 24 * 60 * 60;
}
