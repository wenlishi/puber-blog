package com.puber.blog.utils;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Slug 工具类
 * 用于生成 URL 友好的字符串别名
 *
 * @author puber
 * @version 1.0.0
 * @since 2026-04-13
 */
public class SlugUtils {

    /**
     * 非拉丁字符正则表达式
     */
    private static final Pattern NON_LATIN = Pattern.compile("[^\\w-]");

    /**
     * 连续空白字符正则表达式
     */
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");

    /**
     * 连续连字符正则表达式
     */
    private static final Pattern MULTIPLE_HYPHENS = Pattern.compile("-{2,}");

    /**
     * 生成 Slug
     * 将输入字符串转换为 URL 友好的格式
     *
     * @param input 输入字符串
     * @return String Slug 字符串
     */
    public static String toSlug(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "";
        }

        String slug = input.toLowerCase(Locale.CHINESE)
                .replace(" ", "-")  // 替换空格为连字符
                .replaceAll("[^a-z0-9\\u4e00-\\u9fa5-]", "")  // 移除特殊字符，保留中文、字母、数字和连字符
                .replaceAll("-{2,}", "-")  // 替换连续连字符为单个连字符
                .replaceAll("^-|-$", "");  // 移除开头和结尾的连字符

        return slug;
    }

    /**
     * 生成 Slug（带时间戳）
     * 如果原始 Slug 已存在，可以添加时间戳作为后缀
     *
     * @param input 输入字符串
     * @return String Slug 字符串（带时间戳）
     */
    public static String toSlugWithTimestamp(String input) {
        String slug = toSlug(input);
        if (slug.isEmpty()) {
            return String.valueOf(System.currentTimeMillis());
        }
        return slug + "-" + System.currentTimeMillis();
    }

    /**
     * 从字符串生成数字 ID
     * 使用字符串的哈希值生成一个正整数
     *
     * @param input 输入字符串
     * @return Long 数字 ID
     */
    public static Long generateIdFromString(String input) {
        if (input == null || input.trim().isEmpty()) {
            return 0L;
        }
        return (long) Math.abs(input.hashCode());
    }
}