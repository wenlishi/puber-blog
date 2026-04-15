package com.puber.blog.utils;

/**
 * XSS 过滤策略枚举
 * 针对不同场景使用不同的过滤严格程度
 *
 * @author wenlishi
 * @version 1.0.0
 * @since 2026-04-15
 */
public enum XssPolicy {

    /**
     * 严格策略（适用于前台用户内容：评论、留言）
     * 只保留基础格式标签，移除所有可能的危险内容
     */
    STRICT,

    /**
     * 宽松策略（适用于后台管理员内容：文章、页面）
     * 允许富文本，但过滤最危险的标签和属性
     */
    RELAXED
}