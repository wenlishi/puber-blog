package com.puber.blog.service;

import com.puber.blog.vo.DashboardVO;

import java.util.List;

/**
 * 仪表盘业务服务接口
 * 提供仪表盘统计数据的业务逻辑处理
 *
 * @author wenlishi
 * @version 1.0.0
 * @since 2026-04-13
 */
public interface DashboardService {

    /**
     * 获取仪表盘统计数据
     * 聚合文章、评论、分类、标签的统计数据
     *
     * @return DashboardVO 仪表盘视图对象
     */
    DashboardVO getDashboardData();

    /**
     * 获取访问趋势数据（最近N天）
     *
     * @param days 天数
     * @return List<DailyViewStats> 每日浏览统计列表
     */
    List<DashboardVO.DailyViewStats> getViewTrend(int days);
}