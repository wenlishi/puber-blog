package com.puber.blog.utils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Slug 工具类
 * 用于生成 URL 友好的字符串别名（支持中文转拼音）
 *
 * @author puber
 * @version 1.0.0
 * @since 2026-04-13
 */
public class SlugUtils {

    /**
     * 连续连字符正则表达式
     */
    private static final Pattern MULTIPLE_HYPHENS = Pattern.compile("-{2,}");

    /**
     * 拼音输出格式
     */
    private static final HanyuPinyinOutputFormat PINYIN_FORMAT;

    static {
        PINYIN_FORMAT = new HanyuPinyinOutputFormat();
        PINYIN_FORMAT.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        PINYIN_FORMAT.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        PINYIN_FORMAT.setVCharType(HanyuPinyinVCharType.WITH_V);
    }

    /**
     * 生成 Slug
     * 将输入字符串转换为 URL 友好的格式（中文自动转拼音）
     *
     * @param input 输入字符串
     * @return String Slug 字符串
     */
    public static String toSlug(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "";
        }

        // 中文转拼音
        String slug = convertChineseToPinyin(input.toLowerCase(Locale.CHINESE).trim());

        // 清理和格式化
        slug = slug
                .replaceAll("[^a-z0-9-]", "")  // 移除特殊字符，只保留字母、数字和连字符
                .replaceAll("-{2,}", "-")  // 替换连续连字符为单个连字符
                .replaceAll("^-|-$", "");  // 移除开头和结尾的连字符

        return slug;
    }

    /**
     * 将中文字符转换为拼音
     * 拼音之间不自动添加分隔符，生成紧凑的slug
     * 用户可在编辑时手动调整分隔（如添加短横）
     *
     * @param input 输入字符串
     * @return String 拼音字符串
     */
    private static String convertChineseToPinyin(String input) {
        StringBuilder result = new StringBuilder();

        for (char c : input.toCharArray()) {
            // 判断是否为中文字符
            if (isChinese(c)) {
                try {
                    // 获取拼音数组（一个汉字可能有多音字）
                    String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(c, PINYIN_FORMAT);

                    if (pinyinArray != null && pinyinArray.length > 0) {
                        // 取第一个拼音（最常用的读音），直接连接
                        result.append(pinyinArray[0]);
                    } else {
                        // 如果无法获取拼音，保留原字符
                        result.append(c);
                    }
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    // 转换失败时保留原字符
                    result.append(c);
                }
            } else if (Character.isWhitespace(c)) {
                // 空格替换为连字符
                result.append('-');
            } else {
                // 非中文字符直接保留
                result.append(c);
            }
        }

        return result.toString();
    }

    /**
     * 判断字符是否为中文
     *
     * @param c 字符
     * @return boolean 是否为中文
     */
    private static boolean isChinese(char c) {
        // 中文Unicode范围：基本汉字 + 扩展A区
        // 基本汉字：\u4e00-\u9fa5
        // 扩展A区：\u3400-\u4dbf
        return (c >= '\u4e00' && c <= '\u9fa5') ||
               (c >= '\u3400' && c <= '\u4dbf');
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