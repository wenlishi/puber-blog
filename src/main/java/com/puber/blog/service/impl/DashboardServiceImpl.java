package com.puber.blog.service.impl;

import com.puber.blog.entity.Article;
import com.puber.blog.entity.Comment;
import com.puber.blog.repository.ArticleRepository;
import com.puber.blog.repository.CategoryRepository;
import com.puber.blog.repository.CommentRepository;
import com.puber.blog.repository.TagRepository;
import com.puber.blog.service.DashboardService;
import com.puber.blog.vo.DashboardVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 仪表盘业务服务实现类
 * 实现仪表盘统计数据的业务逻辑处理
 *
 * @author puber
 * @version 1.0.0
 * @since 2026-04-13
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final ArticleRepository articleRepository;
    private final CommentRepository commentRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /**
     * 获取仪表盘统计数据
     * 聚合文章、评论、分类、标签的统计数据
     *
     * @return DashboardVO 仪表盘视图对象
     */
    @Override
    public DashboardVO getDashboardData() {
        log.debug("获取仪表盘统计数据");

        // 1. 文章统计
        DashboardVO.ArticleStats articleStats = buildArticleStats();

        // 2. 评论统计
        DashboardVO.CommentStats commentStats = buildCommentStats();

        // 3. 分类统计
        DashboardVO.CategoryStats categoryStats = buildCategoryStats();

        // 4. 标签统计
        DashboardVO.TagStats tagStats = buildTagStats();

        // 5. 最新文章列表（TOP 5）
        List<DashboardVO.ArticleItemVO> latestArticles = buildLatestArticles();

        // 6. 最新评论列表（TOP 5）
        List<DashboardVO.CommentItemVO> latestComments = buildLatestComments();

        // 7. 热门文章列表（TOP 5）
        List<DashboardVO.ArticleItemVO> hotArticles = buildHotArticles();

        // 构建返回对象
        return DashboardVO.builder()
                .articleStats(articleStats)
                .commentStats(commentStats)
                .categoryStats(categoryStats)
                .tagStats(tagStats)
                .latestArticles(latestArticles)
                .latestComments(latestComments)
                .hotArticles(hotArticles)
                .build();
    }

    /**
     * 构建文章统计信息
     */
    private DashboardVO.ArticleStats buildArticleStats() {
        Long totalCount = articleRepository.count();
        Long publishedCount = articleRepository.countByStatus("PUBLISHED");
        Long draftCount = articleRepository.countByStatus("DRAFT");
        Long totalViewCount = articleRepository.sumViewCount();

        // 处理 null 值
        if (totalViewCount == null) {
            totalViewCount = 0L;
        }

        return DashboardVO.ArticleStats.builder()
                .totalCount(totalCount)
                .publishedCount(publishedCount)
                .draftCount(draftCount)
                .totalViewCount(totalViewCount)
                .build();
    }

    /**
     * 构建评论统计信息
     */
    private DashboardVO.CommentStats buildCommentStats() {
        Long totalCount = commentRepository.count();
        Long pendingCount = commentRepository.countByStatus("PENDING");
        Long approvedCount = commentRepository.countByStatus("APPROVED");
        Long rejectedCount = commentRepository.countByStatus("REJECTED");

        return DashboardVO.CommentStats.builder()
                .totalCount(totalCount)
                .pendingCount(pendingCount)
                .approvedCount(approvedCount)
                .rejectedCount(rejectedCount)
                .build();
    }

    /**
     * 构建分类统计信息
     */
    private DashboardVO.CategoryStats buildCategoryStats() {
        Long totalCount = categoryRepository.count();

        return DashboardVO.CategoryStats.builder()
                .totalCount(totalCount)
                .build();
    }

    /**
     * 构建标签统计信息
     */
    private DashboardVO.TagStats buildTagStats() {
        Long totalCount = tagRepository.count();

        return DashboardVO.TagStats.builder()
                .totalCount(totalCount)
                .build();
    }

    /**
     * 构建最新文章列表（TOP 5）
     */
    private List<DashboardVO.ArticleItemVO> buildLatestArticles() {
        List<Article> articles = articleRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(0, 5));

        return articles.stream()
                .map(article -> DashboardVO.ArticleItemVO.builder()
                        .id(article.getId())
                        .title(article.getTitle())
                        .status(article.getStatus())
                        .viewCount(article.getViewCount())
                        .createdAt(article.getCreatedAt().format(DATE_FORMATTER))
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 构建最新评论列表（TOP 5）
     */
    private List<DashboardVO.CommentItemVO> buildLatestComments() {
        List<Comment> comments = commentRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(0, 5));

        return comments.stream()
                .map(comment -> {
                    // 获取评论对应的文章标题
                    String articleTitle = "未知文章";
                    Article article = articleRepository.findById(comment.getArticleId()).orElse(null);
                    if (article != null) {
                        articleTitle = article.getTitle();
                    }

                    // 截取评论内容（最多50字）
                    String content = comment.getContent();
                    if (content.length() > 50) {
                        content = content.substring(0, 50) + "...";
                    }

                    return DashboardVO.CommentItemVO.builder()
                            .id(comment.getId())
                            .nickname(comment.getNickname())
                            .content(content)
                            .status(comment.getStatus())
                            .articleTitle(articleTitle)
                            .createdAt(comment.getCreatedAt().format(DATE_FORMATTER))
                            .build();
                })
                .collect(Collectors.toList());
    }

    /**
     * 构建热门文章列表（TOP 5）
     */
    private List<DashboardVO.ArticleItemVO> buildHotArticles() {
        List<Article> articles = articleRepository.findAllByOrderByViewCountDesc(PageRequest.of(0, 5));

        return articles.stream()
                .map(article -> DashboardVO.ArticleItemVO.builder()
                        .id(article.getId())
                        .title(article.getTitle())
                        .status(article.getStatus())
                        .viewCount(article.getViewCount())
                        .createdAt(article.getCreatedAt().format(DATE_FORMATTER))
                        .build())
                .collect(Collectors.toList());
    }
}