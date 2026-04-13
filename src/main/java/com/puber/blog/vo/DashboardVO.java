package com.puber.blog.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 仪表盘视图对象
 * 封装仪表盘页面所需的所有统计数据
 *
 * @author puber
 * @version 1.0.0
 * @since 2026-04-13
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardVO {

    /**
     * 文章统计
     */
    private ArticleStats articleStats;

    /**
     * 评论统计
     */
    private CommentStats commentStats;

    /**
     * 分类统计
     */
    private CategoryStats categoryStats;

    /**
     * 标签统计
     */
    private TagStats tagStats;

    /**
     * 最新文章列表（TOP 5）
     */
    private List<ArticleItemVO> latestArticles;

    /**
     * 最新评论列表（TOP 5）
     */
    private List<CommentItemVO> latestComments;

    /**
     * 热门文章列表（TOP 5）
     */
    private List<ArticleItemVO> hotArticles;

    /**
     * 文章统计信息
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ArticleStats {
        /**
         * 文章总数
         */
        private Long totalCount;

        /**
         * 已发布文章数
         */
        private Long publishedCount;

        /**
         * 草稿文章数
         */
        private Long draftCount;

        /**
         * 总浏览量
         */
        private Long totalViewCount;
    }

    /**
     * 评论统计信息
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CommentStats {
        /**
         * 评论总数
         */
        private Long totalCount;

        /**
         * 待审核评论数
         */
        private Long pendingCount;

        /**
         * 已批准评论数
         */
        private Long approvedCount;

        /**
         * 已拒绝评论数
         */
        private Long rejectedCount;
    }

    /**
     * 分类统计信息
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CategoryStats {
        /**
         * 分类总数
         */
        private Long totalCount;
    }

    /**
     * 标签统计信息
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TagStats {
        /**
         * 标签总数
         */
        private Long totalCount;
    }

    /**
     * 文章简要信息
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ArticleItemVO {
        /**
         * 文章ID
         */
        private Long id;

        /**
         * 文章标题
         */
        private String title;

        /**
         * 文章状态
         */
        private String status;

        /**
         * 浏览量
         */
        private Long viewCount;

        /**
         * 创建时间
         */
        private String createdAt;
    }

    /**
     * 评论简要信息
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CommentItemVO {
        /**
         * 评论ID
         */
        private Long id;

        /**
         * 评论者昵称
         */
        private String nickname;

        /**
         * 评论内容（截取）
         */
        private String content;

        /**
         * 评论状态
         */
        private String status;

        /**
         * 文章标题
         */
        private String articleTitle;

        /**
         * 创建时间
         */
        private String createdAt;
    }
}