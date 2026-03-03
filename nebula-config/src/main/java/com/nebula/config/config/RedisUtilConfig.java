package com.nebula.config.config;

import com.nebula.common.util.RedisUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * RedisUtil 配置类
 * 将 RedisUtil 注册为 Spring Bean
 */
@Configuration
public class RedisUtilConfig {

    @Bean
    public RedisUtil redisUtil(RedisTemplate<String, Object> redisTemplate) {
        return new RedisUtil(redisTemplate);
    }
}
