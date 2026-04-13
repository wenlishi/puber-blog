package com.puber.blog.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 文章实体类
 * 对应数据库表：article
 *
 * @author puber
 * @version 1.0.0
 * @since 2026-04-13
 */
@Entity
@Table(name = "article")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Article extends BaseEntity {

    /**
     * 文章标题
     */
    @Column(nullable = false, length = 200)
    private String title;

    /**
     * 文章别名（URL友好）
     * 用于生成友好的 URL
     */
    @Column(nullable = false, unique = true, length = 200)
    private String slug;

    /**
     * 文章摘要
     */
    @Column(columnDefinition = "TEXT")
    private String summary;

    /**
     * 文章内容
     * Markdown 格式
     */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    /**
     * 封面图片 URL
     */
    @Column(length = 255)
    private String coverImage;

    /**
     * 文章状态
     * PUBLISHED: 已发布
     * DRAFT: 草稿
     */
    @Column(nullable = false, length = 20)
    private String status;

    /**
     * 是否置顶
     */
    @Column(nullable = false)
    private Boolean isTop;

    /**
     * 浏览次数
     */
    @Column(nullable = false)
    private Long viewCount;

    /**
     * 是否允许评论
     */
    @Column(nullable = false)
    private Boolean isCommentEnabled;

    /**
     * 作者 ID
     */
    @Column(nullable = false)
    private Long authorId;

    /**
     * 分类 ID
     */
    @Column
    private Long categoryId;

    /**
     * 发布时间
     */
    @Column
    private LocalDateTime publishedAt;
}