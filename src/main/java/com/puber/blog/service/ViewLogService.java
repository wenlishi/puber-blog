package com.puber.blog.service;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 浏览记录业务服务接口
 * 提供浏览记录的记录和统计功能
 *
 * @author wenlishi
 * @version 1.0.0
 * @since 2026-04-19
 */
public interface ViewLogService {

    /**
     * 记录文章浏览日志
     *
     * @param articleId 文章ID
     * @param request HTTP请求对象（获取IP和UserAgent）
     */
    void logArticleView(Long articleId, HttpServletRequest request);

    /**
     * 聚合指定日期的浏览统计数据
     * 将浏览记录表的数据聚合到统计表
     *
     * @param viewDate 统计日期
     */
    void aggregateDailyStats(java.time.LocalDate viewDate);

    /**
     * 聚合昨天的浏览统计数据
     * 定时任务调用此方法
     */
    void aggregateYesterdayStats();

    /**
     * 获取最近N天的全站浏览趋势数据
     * 从统计表查询，提高性能
     *
     * @param days 天数
     * @return List<ViewStats> 浏览趋势数据
     */
    java.util.List<com.puber.blog.entity.ViewStats> getRecentViewTrend(int days);
}