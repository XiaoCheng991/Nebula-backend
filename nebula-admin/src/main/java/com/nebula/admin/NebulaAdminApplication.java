package com.nebula.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * NebulaAdmin启动类
 */
@SpringBootApplication(scanBasePackages = "com.nebula")
@MapperScan("com.nebula.service.mapper")
@EnableFeignClients(basePackages = "com.nebula.api.feign")
public class NebulaAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(NebulaAdminApplication.class, args);
        System.out.println("""

                ========================================
                   NebulaHub Admin Started Successfully!
                   API Doc: http://localhost:8080/doc.html
                ========================================
                """);
    }
}
