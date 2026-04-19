package com.puber.blog.controller.admin;

import com.puber.blog.service.ViewLogService;
import com.puber.blog.vo.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * 后台统计数据管理控制器
 * 提供手动触发统计聚合等功能
 *
 * @author wenlishi
 * @version 1.0.0
 * @since 2026-04-19
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/stats")
@RequiredArgsConstructor
public class StatsController {

    private final ViewLogService viewLogService;

    /**
     * 手动聚合指定日期的统计数据
     * 用于测试或补齐缺失的统计数据
     *
     * @param date 日期（格式：yyyy-MM-dd），默认为今天
     * @return Result<Void> 操作结果
     */
    @PostMapping("/aggregate")
    public Result<Void> aggregateStats(@RequestParam(required = false) String date) {
        log.info("手动聚合统计数据：date={}", date);

        LocalDate targetDate;
        if (date != null && !date.isEmpty()) {
            targetDate = LocalDate.parse(date);
        } else {
            targetDate = LocalDate.now();
        }

        viewLogService.aggregateDailyStats(targetDate);

        return Result.success();
    }

    /**
     * 手动聚合最近7天的统计数据
     * 用于初始化或修复统计数据
     *
     * @return Result<Void> 操作结果
     */
    @PostMapping("/aggregate-recent")
    public Result<Void> aggregateRecentStats() {
        log.info("手动聚合最近7天统计数据");

        LocalDate today = LocalDate.now();
        for (int i = 0; i < 7; i++) {
            LocalDate targetDate = today.minusDays(i);
            viewLogService.aggregateDailyStats(targetDate);
        }

        return Result.success();
    }
}