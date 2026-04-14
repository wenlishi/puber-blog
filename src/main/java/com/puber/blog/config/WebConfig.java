package com.puber.blog.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置类
 * 配置静态资源映射、拦截器等
 *
 * @author puber
 * @version 1.0.0
 * @since 2026-04-13
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * 配置静态资源映射
     * 将上传的文件映射为静态资源，可以直接通过 URL 访问
     *
     * @param registry 资源处理器注册器
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 映射上传文件路径
        // 访问路径：/uploads/**
        // 实际路径：E:/Desktop/puber-blog/uploads/
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:E:/Desktop/puber-blog/uploads/");
    }
}