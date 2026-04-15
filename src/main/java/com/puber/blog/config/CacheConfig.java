package com.puber.blog.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * 缓存配置类
 * 配置 Caffeine 本地缓存策略
 *
 * @author puber
 * @version 1.0.0
 * @since 2026-04-14
 */
@Configuration
public class CacheConfig {

    /**
     * 配置 Caffeine 缓存管理器
     * 使用默认缓存策略，适用于大部分场景
     *
     * @return CacheManager 缓存管理器
     */
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();

        // 设置默认缓存配置：最大1000条记录，写入后10分钟过期
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(600, TimeUnit.SECONDS)
                .recordStats());

        return cacheManager;
    }

    /**
     * 系统配置缓存（更新频率低，1小时过期）
     */
    @Bean
    public Caffeine<Object, Object> siteInfoCaffeine() {
        return Caffeine.newBuilder()
                .maximumSize(100)
                .expireAfterWrite(3600, TimeUnit.SECONDS)
                .recordStats();
    }

    /**
     * 分类缓存（更新频率中等，30分钟过期）
     */
    @Bean
    public Caffeine<Object, Object> categoriesCaffeine() {
        return Caffeine.newBuilder()
                .maximumSize(200)
                .expireAfterWrite(1800, TimeUnit.SECONDS)
                .recordStats();
    }

    /**
     * 标签缓存（更新频率中等，30分钟过期）
     */
    @Bean
    public Caffeine<Object, Object> tagsCaffeine() {
        return Caffeine.newBuilder()
                .maximumSize(200)
                .expireAfterWrite(1800, TimeUnit.SECONDS)
                .recordStats();
    }

    /**
     * 热门文章缓存（需要频繁更新，5分钟过期）
     */
    @Bean
    public Caffeine<Object, Object> hotArticlesCaffeine() {
        return Caffeine.newBuilder()
                .maximumSize(50)
                .expireAfterWrite(300, TimeUnit.SECONDS)
                .recordStats();
    }
}