package com.puber.blog.controller.admin;

import com.puber.blog.service.DashboardService;
import com.puber.blog.vo.DashboardVO;
import com.puber.blog.vo.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 后台仪表盘API控制器
 * 提供仪表盘统计数据的REST API
 *
 * @author wenlishi
 * @version 1.0.0
 * @since 2026-04-19
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * 获取访问趋势数据
     *
     * @param days 天数（7或30）
     * @return Result<List<DailyViewStats>> 趋势数据列表
     */
    @GetMapping("/view-trend")
    public Result<List<DashboardVO.DailyViewStats>> getViewTrend(@RequestParam(defaultValue = "7") int days) {
        log.info("获取访问趋势数据：days={}", days);

        List<DashboardVO.DailyViewStats> viewTrend = dashboardService.getViewTrend(days);
        return Result.success(viewTrend);
    }
}