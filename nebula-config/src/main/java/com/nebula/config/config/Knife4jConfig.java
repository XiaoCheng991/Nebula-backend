package com.nebula.config.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Knife4j配置
 */
@Configuration
public class Knife4jConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("NebulaHub API文档")
                        .version("1.0.0")
                        .description("NebulaHub后端API接口文档")
                        .contact(new Contact()
                                .name("NebulaHub Team")
                                .email("contact@nebulahub.com")));
    }
}
