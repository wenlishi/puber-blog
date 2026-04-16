package com.puber.blog.utils;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

/**
 * Markdown 工具类
 * 用于将 Markdown 文本转换为 HTML
 * 支持不同的 XSS 过滤策略
 *
 * @author wenlishi
 * @version 1.0.0
 * @since 2026-04-13
 */
public class MarkdownUtils {

    /**
     * Markdown 解析器
     */
    private static final Parser parser = Parser.builder().build();

    /**
     * HTML 渲染器
     */
    private static final HtmlRenderer renderer = HtmlRenderer.builder().build();

    /**
     * 处理短代码预处理
     * 将{{demo:slug}}替换为iframe标签
     *
     * @param markdown Markdown 文本
     * @return String 处理后的 Markdown 文本
     */
    private static String processShortcodes(String markdown) {
        if (markdown == null || markdown.trim().isEmpty()) {
            return "";
        }

        // 正则匹配 {{demo:slug}} 短代码
        // 支持：{{demo:button-animation}}、{{demo:card-flip}}等
        String pattern = "\\{\\{demo:([a-zA-Z0-9-]+)\\}\\}";

        // 替换为 iframe 标签
        // 使用同源 src="/demo/{slug}/embed" (精简模板，无框架)
        // 添加响应式样式和 sandbox 安全属性
        String processed = markdown.replaceAll(pattern,
                "<div class=\"demo-container\">\n" +
                "  <iframe src=\"/demo/$1/embed\" " +
                "class=\"demo-iframe\" " +
                "width=\"100%\" " +
                "height=\"400\" " +
                "frameborder=\"0\" " +
                "sandbox=\"allow-scripts allow-same-origin\" " +
                "loading=\"lazy\">\n" +
                "  </iframe>\n" +
                "</div>"
        );

        return processed;
    }

    /**
     * 将 Markdown 文本转换为 HTML（使用宽松策略）
     * 适用于后台管理员发布的文章内容
     *
     * @param markdown Markdown 文本
     * @return String 安全的 HTML 文本
     */
    public static String toHtml(String markdown) {
        if (markdown == null || markdown.trim().isEmpty()) {
            return "";
        }

        // 1. 首先处理短代码（替换为 iframe）
        String processed = processShortcodes(markdown);

        // 2. 然后转换为 HTML
        Node document = parser.parse(processed);
        String html = renderer.render(document);

        // 3. 最后进行 XSS 过滤（RELAXED 策略已支持 iframe）
        return XssUtils.sanitize(html, XssPolicy.RELAXED);
    }

    /**
     * 将 Markdown 文本转换为 HTML（根据策略过滤 XSS）
     *
     * @param markdown Markdown 文本
     * @param policy   XSS 过滤策略
     * @return String 安全的 HTML 文本
     */
    public static String toHtml(String markdown, XssPolicy policy) {
        if (markdown == null || markdown.trim().isEmpty()) {
            return "";
        }
        Node document = parser.parse(markdown);
        String html = renderer.render(document);

        // XSS过滤：根据策略过滤危险内容
        return XssUtils.sanitize(html, policy);
    }

    /**
     * 从 Markdown 文本中提取摘要
     * 默认提取前 200 个字符
     *
     * @param markdown Markdown 文本
     * @return String 文章摘要
     */
    public static String extractSummary(String markdown) {
        return extractSummary(markdown, 200);
    }

    /**
     * 从 Markdown 文本中提取摘要
     *
     * @param markdown Markdown 文本
     * @param length 摘要长度
     * @return String 文章摘要
     */
    public static String extractSummary(String markdown, int length) {
        if (markdown == null || markdown.trim().isEmpty()) {
            return "";
        }
        // 移除 Markdown 标记符号，提取纯文本
        String text = markdown
                .replaceAll("#+ ", "")  // 移除标题标记
                .replaceAll("\\*\\*", "")  // 移除粗体标记
                .replaceAll("\\*", "")  // 移除斜体标记
                .replaceAll("`+", "")  // 移除代码标记
                .replaceAll("!\\[.*?\\]\\(.*?\\)", "")  // 移除图片
                .replaceAll("\\[.*?\\]\\(.*?\\)", "")  // 移除链接
                .replaceAll("\n+", " ")  // 替换换行为空格
                .trim();

        if (text.length() <= length) {
            return text;
        }
        return text.substring(0, length) + "...";
    }
}