package com.puber.blog.utils;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

/**
 * Markdown 工具类
 * 用于将 Markdown 文本转换为 HTML
 *
 * @author puber
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
     * 将 Markdown 文本转换为 HTML
     *
     * @param markdown Markdown 文本
     * @return String HTML 文本
     */
    public static String toHtml(String markdown) {
        if (markdown == null || markdown.trim().isEmpty()) {
            return "";
        }
        Node document = parser.parse(markdown);
        return renderer.render(document);
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