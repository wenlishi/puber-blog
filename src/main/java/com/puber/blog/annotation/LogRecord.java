package com.puber.blog.annotation;

import java.lang.annotation.*;

/**
 * 业务操作日志记录注解
 * 标注在方法上，AOP切面自动拦截并记录操作日志
 *
 * 使用示例：
 * @LogRecord(operation = "发布文章", level = LogLevel.INFO)
 * public Article publishArticle(ArticleDTO dto) {
 *     // 业务逻辑
 * }
 *
 * 自动记录内容：
 * - 操作名称：发布文章
 * - 执行时间：125ms
 * - 操作参数：{title=..., content=...}
 * - 操作结果：成功/失败
 * - 异常信息：如有异常自动捕获
 *
 * @author wenlishi
 * @version 1.0.0
 * @since 2026-04-14
 */
@Target(ElementType.METHOD)               // 作用于方法
@Retention(RetentionPolicy.RUNTIME)       // 运行时保留
@Documented
public @interface LogRecord {

    /**
     * 操作名称
     * 例如："发布文章"、"提交评论"、"审核评论"
     */
    String operation() default "";

    /**
     * 日志级别
     * 默认INFO级别，重要操作可设置为WARN或ERROR
     */
    LogLevel level() default LogLevel.INFO;

    /**
     * 是否记录方法参数
     * 默认记录，敏感信息（密码等）建议关闭
     */
    boolean recordParams() default true;

    /**
     * 是否记录方法返回值
     * 默认不记录，避免日志过大
     */
    boolean recordResult() default false;

    /**
     * 是否记录执行时间
     * 默认记录，用于性能监控
     */
    boolean recordTime() default true;

    /**
     * 日志级别枚举
     */
    enum LogLevel {
        TRACE,  // 最详细追踪
        DEBUG,  // 开发调试
        INFO,   // 重要业务流程（推荐）
        WARN,   // 潜在问题警告
        ERROR   // 严重错误
    }
}