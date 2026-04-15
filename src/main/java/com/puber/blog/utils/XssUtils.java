package com.puber.blog.utils;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

/**
 * XSS 防护工具类
 * 提供不同严格程度的 HTML 过滤策略
 *
 * @author wenlishi
 * @version 1.0.0
 * @since 2026-04-15
 */
public class XssUtils {

    /**
     * 严格白名单（用于前台用户内容）
     * 只允许基础文本格式化标签
     */
    private static final Safelist STRICT_WHITELIST = Safelist.relaxed()
            // 移除所有链接（防止钓鱼）
            .removeTags("a")
            // 移除图片（防止恶意图片攻击）
            .removeTags("img")
            // 只保留基础格式
            .addTags("p", "br", "strong", "em", "u", "blockquote", "code", "pre");

    /**
     * 宽松白名单（用于后台管理员内容）
     * 允许富文本标签，但过滤最危险的内容
     */
    private static final Safelist RELAXED_WHITELIST = Safelist.relaxed()
            // 允许链接，但强制添加 rel="nofollow noopener"
            .addAttributes("a", "href", "title", "target")
            // 允许图片
            .addAttributes("img", "src", "alt", "title", "width", "height")
            // 允许 iframe（用于嵌入视频等），但强制安全属性
            .addTags("iframe")
            .addAttributes("iframe", "src", "width", "height", "frameborder", "allowfullscreen")
            // 允许代码块和样式
            .addTags("pre", "code", "span", "div")
            .addAttributes("div", "class", "style")
            .addAttributes("span", "class", "style");

    /**
     * 根据策略过滤 HTML 内容
     *
     * @param html   原始 HTML
     * @param policy XSS 过滤策略
     * @return String 安全的 HTML
     */
    public static String sanitize(String html, XssPolicy policy) {
        if (html == null || html.trim().isEmpty()) {
            return "";
        }

        Safelist safelist = (policy == XssPolicy.STRICT)
                ? STRICT_WHITELIST
                : RELAXED_WHITELIST;

        // 使用 Jsoup 进行白名单过滤
        String cleanedHtml = Jsoup.clean(html, safelist);

        // 额外的安全措施：强制添加 rel 属性到所有链接
        if (policy == XssPolicy.RELAXED) {
            cleanedHtml = cleanedHtml.replaceAll(
                    "<a href=",
                    "<a rel=\"nofollow noopener noreferrer\" href="
            );
        }

        return cleanedHtml;
    }

    /**
     * 严格过滤（用于前台用户内容）
     * 快捷方法
     *
     * @param html 原始 HTML
     * @return String 安全的 HTML
     */
    public static String sanitizeStrict(String html) {
        return sanitize(html, XssPolicy.STRICT);
    }

    /**
     * 宽松过滤（用于后台管理员内容）
     * 快捷方法
     *
     * @param html 原始 HTML
     * @return String 安全的 HTML
     */
    public static String sanitizeRelaxed(String html) {
        return sanitize(html, XssPolicy.RELAXED);
    }
}