package com.puber.blog.service.impl;

import com.puber.blog.entity.Article;
import com.puber.blog.entity.Comment;
import com.puber.blog.entity.ViewStats;
import com.puber.blog.repository.ArticleRepository;
import com.puber.blog.repository.CategoryRepository;
import com.puber.blog.repository.CommentRepository;
import com.puber.blog.repository.MessageRepository;
import com.puber.blog.repository.TagRepository;
import com.puber.blog.repository.ViewLogRepository;
import com.puber.blog.repository.ViewStatsRepository;
import com.puber.blog.service.DashboardService;
import com.puber.blog.service.ViewLogService;
import com.puber.blog.vo.DashboardVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 仪表盘业务服务实现类
 * 实现仪表盘统计数据的业务逻辑处理
 *
 * @author wenlishi
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
    private final MessageRepository messageRepository;
    private final ViewStatsRepository viewStatsRepository;
    private final ViewLogRepository viewLogRepository;
    private final ViewLogService viewLogService;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final DateTimeFormatter DATE_LABEL_FORMATTER = DateTimeFormatter.ofPattern("MM-dd");

    /**
     * 获取仪表盘统计数据
     * 聚合文章、评论、分类、标签、留言的统计数据
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

        // 5. 留言统计
        DashboardVO.MessageStats messageStats = buildMessageStats();

        // 6. 访问趋势数据（最近7天）
        List<DashboardVO.DailyViewStats> viewTrend = buildViewTrend(7);

        // 7. 最新文章列表（TOP 5）
        List<DashboardVO.ArticleItemVO> latestArticles = buildLatestArticles();

        // 8. 最新评论列表（TOP 5）
        List<DashboardVO.CommentItemVO> latestComments = buildLatestComments();

        // 9. 热门文章列表（TOP 5）
        List<DashboardVO.ArticleItemVO> hotArticles = buildHotArticles();

        // 构建返回对象
        return DashboardVO.builder()
                .articleStats(articleStats)
                .commentStats(commentStats)
                .categoryStats(categoryStats)
                .tagStats(tagStats)
                .messageStats(messageStats)
                .viewTrend(viewTrend)
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
     * 构建留言统计信息
     */
    private DashboardVO.MessageStats buildMessageStats() {
        Long totalCount = messageRepository.count();
        Long pendingCount = messageRepository.countByStatus("PENDING");
        Long approvedCount = messageRepository.countByStatus("APPROVED");
        Long rejectedCount = messageRepository.countByStatus("REJECTED");

        return DashboardVO.MessageStats.builder()
                .totalCount(totalCount)
                .pendingCount(pendingCount)
                .approvedCount(approvedCount)
                .rejectedCount(rejectedCount)
                .build();
    }

    /**
     * 获取访问趋势数据（最近N天）
     * 公开方法，供DashboardController调用
     * 实时查询：今天的浏览数据直接从view_log实时计算
     * 历史数据：从view_stats统计表查询（定时任务已聚合）
     *
     * @param days 天数
     * @return 每日浏览统计列表
     */
    @Override
    public List<DashboardVO.DailyViewStats> getViewTrend(int days) {
        log.debug("获取访问趋势数据：days={}", days);
        return buildViewTrend(days);
    }

    /**
     * 构建访问趋势数据（最近N天）
     * 智能查询策略：
     * - 今天的数据：实时从view_log计算（保证实时性）
     * - 历史数据：从view_stats统计表查询（保证性能）
     * - 即使某些日期没有数据，也返回完整的N天数据，缺失日期填充0
     *
     * @param days 天数
     * @return 每日浏览统计列表
     */
    private List<DashboardVO.DailyViewStats> buildViewTrend(int days) {
        log.debug("构建访问趋势数据：days={}", days);

        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(days - 1);

        // 从统计表查询历史数据（不包括今天）
        LocalDate yesterday = today.minusDays(1);
        List<ViewStats> statsList = viewStatsRepository.findByViewDateBetweenAndStatsType(startDate, yesterday, "SITE");

        // 构建完整的日期列表
        List<DashboardVO.DailyViewStats> trendList = new ArrayList<>();
        for (int i = days - 1; i >= 0; i--) {
            LocalDate targetDate = today.minusDays(i);
            String dateLabel = targetDate.format(DATE_LABEL_FORMATTER);

            Long viewCount;
            Long visitorCount;
            if (targetDate.equals(today)) {
                // 今天的数据实时从view_log查询
                viewCount = viewLogRepository.countByViewDate(today);
                visitorCount = viewLogRepository.countUniqueVisitorsByViewDate(today);
                log.debug("实时查询今天数据：date={}, viewCount={}, visitorCount={}", targetDate, viewCount, visitorCount);
            } else {
                // 历史数据从统计表查询
                ViewStats dayStats = statsList.stream()
                        .filter(stats -> stats.getViewDate().equals(targetDate))
                        .findFirst()
                        .orElse(null);

                viewCount = dayStats != null ? dayStats.getViewCount() : 0L;
                visitorCount = dayStats != null && dayStats.getUniqueVisitors() != null ? dayStats.getUniqueVisitors() : 0L;
            }

            trendList.add(DashboardVO.DailyViewStats.builder()
                    .dateLabel(dateLabel)
                    .viewCount(viewCount)
                    .visitorCount(visitorCount)
                    .build());
        }

        return trendList;
    }

    /**
     * 从ViewLog实时构建访问趋势数据（回退方案）
     * 当统计表无数据时使用此方法
     *
     * @param days 天数
     * @return 每日浏览统计列表
     */
    private List<DashboardVO.DailyViewStats> buildViewTrendFromViewLog(int days) {
        List<DashboardVO.DailyViewStats> trendList = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (int i = days - 1; i >= 0; i--) {
            LocalDate targetDate = today.minusDays(i);
            String dateLabel = targetDate.format(DATE_LABEL_FORMATTER);

            // 实时查询当天的浏览量（性能较差，仅作为回退方案）
            Long dayViewCount = viewLogService.getRecentViewTrend(1).stream()
                    .filter(stats -> stats.getViewDate().equals(targetDate))
                    .mapToLong(ViewStats::getViewCount)
                    .findFirst()
                    .orElse(0L);

            trendList.add(DashboardVO.DailyViewStats.builder()
                    .dateLabel(dateLabel)
                    .viewCount(dayViewCount)
                    .build());
        }

        return trendList;
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
                        .createdAt(article.getCreatedAt() != null ? article.getCreatedAt().format(DATE_FORMATTER) : "")
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
                    if (comment.getArticleId() != null) {
                        Article article = articleRepository.findById(comment.getArticleId()).orElse(null);
                        if (article != null) {
                            articleTitle = article.getTitle();
                        }
                    }

                    // 截取评论内容（最多50字）
                    String content = comment.getContent() != null ? comment.getContent() : "";
                    if (content.length() > 50) {
                        content = content.substring(0, 50) + "...";
                    }

                    return DashboardVO.CommentItemVO.builder()
                            .id(comment.getId())
                            .nickname(comment.getNickname() != null ? comment.getNickname() : "匿名")
                            .content(content)
                            .status(comment.getStatus())
                            .articleTitle(articleTitle)
                            .createdAt(comment.getCreatedAt() != null ? comment.getCreatedAt().format(DATE_FORMATTER) : "")
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
                        .viewCount(article.getViewCount() != null ? article.getViewCount() : 0)
                        .createdAt(article.getCreatedAt() != null ? article.getCreatedAt().format(DATE_FORMATTER) : "")
                        .build())
                .collect(Collectors.toList());
    }
}