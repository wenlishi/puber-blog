package com.puber.blog.service;

import com.puber.blog.vo.DashboardVO;

/**
 * 仪表盘业务服务接口
 * 提供仪表盘统计数据的业务逻辑处理
 *
 * @author puber
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
}