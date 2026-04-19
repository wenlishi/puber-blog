package com.puber.blog.service.impl;

import com.puber.blog.annotation.LogRecord;
import com.puber.blog.annotation.LogRecord.LogLevel;
import com.puber.blog.entity.ViewLog;
import com.puber.blog.entity.ViewStats;
import com.puber.blog.repository.ViewLogRepository;
import com.puber.blog.repository.ViewStatsRepository;
import com.puber.blog.service.ViewLogService;
import com.puber.blog.utils.IpUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 浏览记录业务服务实现类
 * 实现浏览记录的记录和统计功能
 *
 * @author wenlishi
 * @version 1.0.0
 * @since 2026-04-19
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ViewLogServiceImpl implements ViewLogService {

    private final ViewLogRepository viewLogRepository;
    private final ViewStatsRepository viewStatsRepository;

    /**
     * 记录文章浏览日志
     * 异步执行，不阻塞主流程
     *
     * @param articleId 文章ID
     * @param request HTTP请求对象
     */
    @Override
    @Transactional
    @LogRecord(operation = "记录浏览日志", level = LogLevel.DEBUG, recordParams = false, recordTime = true)
    public void logArticleView(Long articleId, HttpServletRequest request) {
        log.debug("记录文章浏览：articleId={}", articleId);

        String ipAddress = IpUtils.getIpAddress(request);
        String userAgent = request.getHeader("User-Agent");
        String sessionId = request.getSession() != null ? request.getSession().getId() : null;

        ViewLog viewLog = ViewLog.builder()
                .articleId(articleId)
                .viewTime(LocalDateTime.now())
                .viewDate(LocalDate.now())
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .sessionId(sessionId)
                .build();

        viewLogRepository.save(viewLog);
    }

    /**
     * 聚合指定日期的浏览统计数据
     * 将浏览记录表的数据聚合到统计表
     *
     * @param viewDate 统计日期
     */
    @Override
    @Transactional
    @CacheEvict(value = "viewTrend", allEntries = true)
    @LogRecord(operation = "聚合浏览统计", level = LogLevel.INFO, recordParams = true, recordTime = true)
    public void aggregateDailyStats(LocalDate viewDate) {
        log.info("聚合浏览统计数据：date={}", viewDate);

        // 统计全站浏览量
        Long totalViewCount = viewLogRepository.countByViewDate(viewDate);
        Long uniqueVisitors = viewLogRepository.countUniqueVisitorsByViewDate(viewDate);

        // 检查是否已存在该日期的统计数据
        ViewStats existingStats = viewStatsRepository.findByViewDateAndStatsType(viewDate, "SITE").orElse(null);

        if (existingStats != null) {
            // 更新已有数据
            existingStats.setViewCount(totalViewCount);
            existingStats.setUniqueVisitors(uniqueVisitors);
            viewStatsRepository.save(existingStats);
        } else {
            // 创建新统计记录
            ViewStats viewStats = ViewStats.builder()
                    .viewDate(viewDate)
                    .viewCount(totalViewCount)
                    .uniqueVisitors(uniqueVisitors)
                    .statsType("SITE")
                    .build();

            viewStatsRepository.save(viewStats);
        }

        // TODO: 可以扩展聚合单篇文章的统计数据
        log.info("浏览统计聚合完成：date={}, viewCount={}, uniqueVisitors={}", viewDate, totalViewCount, uniqueVisitors);
    }

    /**
     * 聚合昨天的浏览统计数据
     * 定时任务每天凌晨1点执行
     */
    @Override
    @Scheduled(cron = "0 0 1 * * ?")  // 每天凌晨1点执行
    @Transactional
    public void aggregateYesterdayStats() {
        log.info("定时任务：聚合昨天的浏览统计数据");

        LocalDate yesterday = LocalDate.now().minusDays(1);
        aggregateDailyStats(yesterday);
    }

    /**
     * 获取最近N天的全站浏览趋势数据
     * 使用缓存优化性能
     *
     * @param days 天数
     * @return List<ViewStats> 浏览趋势数据
     */
    @Override
    @Cacheable(value = "viewTrend", key = "#days")
    public List<ViewStats> getRecentViewTrend(int days) {
        log.debug("获取最近{}天浏览趋势数据", days);

        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days - 1);

        List<ViewStats> statsList = viewStatsRepository.findByViewDateBetweenAndStatsType(startDate, endDate, "SITE");

        // 如果统计表中没有数据，说明定时任务还未执行过，返回空列表或从view_log实时计算
        if (statsList.isEmpty()) {
            log.warn("统计表中没有数据，可能定时任务还未执行");
            // 可以选择从view_log实时查询，但这会比较慢
        }

        return statsList;
    }
}