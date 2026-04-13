package com.puber.blog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * puber-blog 应用程序主类
 *
 * @SpringBootApplication Spring Boot 自动配置
 * @EnableJpaAuditing 启用 JPA 审计功能（自动填充创建时间、更新时间等字段）
 *
 * @author puber
 * @version 1.0.0
 * @since 2026-04-13
 */
@SpringBootApplication
@EnableJpaAuditing
public class PuberBlogApplication {

    /**
     * 应用程序入口方法
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(PuberBlogApplication.class, args);
    }
}