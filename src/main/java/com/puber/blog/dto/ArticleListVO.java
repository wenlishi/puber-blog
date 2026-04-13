package com.puber.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 文章列表视图对象（VO）
 * 用于返回给前端展示的文章列表数据（简化版）
 *
 * @author puber
 * @version 1.0.0
 * @since 2026-04-13
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArticleListVO {

    /**
     * 文章ID
     */
    private Long id;

    /**
     * 文章标题
     */
    private String title;

    /**
     * 文章别名（URL友好）
     */
    private String slug;

    /**
     * 文章摘要
     */
    private String summary;

    /**
     * 封面图片URL
     */
    private String coverImage;

    /**
     * 文章状态
     */
    private String status;

    /**
     * 是否置顶
     */
    private Boolean isTop;

    /**
     * 浏览次数
     */
    private Long viewCount;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 分类别名（用于URL）
     */
    private String categorySlug;

    /**
     * 标签名称列表（逗号分隔）
     */
    private String tagNames;

    /**
     * 作者名称
     */
    private String authorName;

    /**
     * 发布时间
     */
    private LocalDateTime publishedAt;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}