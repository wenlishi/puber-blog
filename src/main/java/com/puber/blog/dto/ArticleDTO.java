package com.puber.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 文章数据传输对象（DTO）
 * 用于接收前端提交的文章数据（创建和编辑）
 *
 * @author puber
 * @version 1.0.0
 * @since 2026-04-13
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArticleDTO {

    /**
     * 文章标题
     */
    private String title;

    /**
     * 文章别名（URL友好）
     * 如果不提供，系统会自动根据title生成
     */
    private String slug;

    /**
     * 文章摘要
     * 如果不提供，系统会从content自动生成
     */
    private String summary;

    /**
     * 文章内容（Markdown格式）
     */
    private String content;

    /**
     * 封面图片URL
     */
    private String coverImage;

    /**
     * 文章状态
     * PUBLISHED: 已发布
     * DRAFT: 草稿
     */
    private String status;

    /**
     * 是否置顶
     */
    private Boolean isTop;

    /**
     * 是否允许评论
     */
    private Boolean isCommentEnabled;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 标签ID列表
     */
    private List<Long> tagIds;
}