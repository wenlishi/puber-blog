package com.puber.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 文章视图对象（VO）
 * 用于返回给前端展示的文章详情数据
 *
 * @author puber
 * @version 1.0.0
 * @since 2026-04-13
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArticleVO {

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
     * 文章内容（Markdown转换为HTML）
     */
    private String contentHtml;

    /**
     * 文章原始内容（Markdown格式）
     */
    private String contentMarkdown;

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
     * 是否允许评论
     */
    private Boolean isCommentEnabled;

    /**
     * 分类信息
     */
    private CategoryVO category;

    /**
     * 标签列表
     */
    private List<TagVO> tags;

    /**
     * 标签名称列表（逗号分隔，用于SEO）
     */
    private String tagNames;

    /**
     * 作者信息
     */
    private AuthorVO author;

    /**
     * 发布时间
     */
    private LocalDateTime publishedAt;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 作者信息VO（内部类）
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AuthorVO {
        private Long id;
        private String username;
        private String nickname;
        private String avatar;
        private String bio;
    }
}