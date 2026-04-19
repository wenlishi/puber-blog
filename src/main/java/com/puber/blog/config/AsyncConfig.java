package com.puber.blog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * 异步任务配置类
 * 用于配置异步邮件发送等任务的线程池
 * 同时启用定时任务功能
 *
 * @author wenlishi
 * @version 1.0.0
 * @since 2026-04-14
 */
@Configuration
@EnableAsync
@EnableScheduling  // 启用定时任务
public class AsyncConfig {

    /**
     * 配置邮件发送专用线程池
     * 独立的线程池避免影响其他异步任务
     *
     * @return Executor 线程池执行器
     */
    @Bean(name = "mailTaskExecutor")
    public Executor mailTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // 核心线程数：2个（邮件发送不需要太多并发）
        executor.setCorePoolSize(2);

        // 最大线程数：5个（突发情况下可以增加）
        executor.setMaxPoolSize(5);

        // 阵列容量：10个（等待发送的邮件队列）
        executor.setQueueCapacity(10);

        // 线程名称前缀（方便日志识别）
        executor.setThreadNamePrefix("Mail-Async-");

        // 线程空闲时间（秒）
        executor.setKeepAliveSeconds(60);

        // 拒绝策略： CallerRunsPolicy（队列满时由调用线程执行）
        executor.setRejectedExecutionHandler(new java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy());

        // 等待所有任务完成后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);

        // 等待时间（秒）
        executor.setAwaitTerminationSeconds(30);

        executor.initialize();
        return executor;
    }
}