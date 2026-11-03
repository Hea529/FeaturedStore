package com.preferredmall;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Preferred Mall 启动类（模块化单体）
 */
@EnableAsync
@MapperScan("com.preferredmall.**.mapper")
@ConfigurationPropertiesScan(basePackages = "com.preferredmall.infra.property")
@SpringBootApplication(scanBasePackages = "com.preferredmall")
public class UniAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(UniAppApplication.class, args);
        System.out.println("\n=== Preferred Mall started, Knife4j 接口文档: http://localhost:8080/doc.html ===\n");
    }
}
