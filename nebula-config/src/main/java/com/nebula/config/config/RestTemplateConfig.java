package com.nebula.config.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * RestTemplate配置
 */
@Configuration
public class RestTemplateConfig {

    /**
     * 默认RestTemplate（用于与前端通信，驼峰命名）
     */
    @Bean
    @Primary
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    /**
     * 用于GitHub API的RestTemplate（支持下划线转驼峰）
     */
    @Bean("githubRestTemplate")
    public RestTemplate githubRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        // 配置支持下划线转驼峰的ObjectMapper
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // 替换现有的JSON转换器
        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        converters.add(new MappingJackson2HttpMessageConverter(objectMapper));

        // 添加其他默认转换器
        restTemplate.getMessageConverters().forEach(converter -> {
            if (!(converter instanceof MappingJackson2HttpMessageConverter)) {
                converters.add(converter);
            }
        });

        restTemplate.setMessageConverters(converters);
        return restTemplate;
    }
}
