package com.puber.blog.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.concurrent.TimeUnit;

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
     * 配置浏览器缓存策略，减少重复请求，提升页面加载速度
     *
     * @param registry 资源处理器注册器
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 映射上传文件路径（图片等）
        // 访问路径：/uploads/**
        // 实际路径：E:/Desktop/puber-blog/uploads/
        // 缓存策略：365天（图片几乎不变，长期缓存）
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:E:/Desktop/puber-blog/uploads/")
                .setCacheControl(CacheControl.maxAge(365, TimeUnit.DAYS)
                        .cachePublic());

        // 映射静态资源路径（CSS/JS/字体等）
        // 访问路径：/static/**
        // 实际路径：classpath:/static/
        // 缓存策略：30天（静态资源稳定，中期缓存）
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/")
                .setCacheControl(CacheControl.maxAge(30, TimeUnit.DAYS)
                        .cachePublic());
    }
}