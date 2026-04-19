package com.puber.blog.repository;

import com.puber.blog.entity.ViewStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 浏览统计 Repository 接口
 *
 * @author wenlishi
 * @version 1.0.0
 * @since 2026-04-19
 */
@Repository
public interface ViewStatsRepository extends JpaRepository<ViewStats, Long> {

    /**
     * 根据日期查询全站统计
     *
     * @param viewDate 统计日期
     * @param statsType 统计类型（SITE）
     * @return Optional<ViewStats> 统计数据
     */
    Optional<ViewStats> findByViewDateAndStatsType(LocalDate viewDate, String statsType);

    /**
     * 根据日期范围查询全站统计
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param statsType 统计类型（SITE）
     * @return List<ViewStats> 统计数据列表
     */
    List<ViewStats> findByViewDateBetweenAndStatsType(LocalDate startDate, LocalDate endDate, String statsType);

    /**
     * 查询最近N天的全站统计（按日期倒序）
     *
     * @param statsType 统计类型（SITE）
     * @param limit 限制数量
     * @return List<ViewStats> 统计数据列表
     */
    @Query("SELECT v FROM ViewStats v WHERE v.statsType = :statsType ORDER BY v.viewDate DESC LIMIT :limit")
    List<ViewStats> findRecentStats(@Param("statsType") String statsType, @Param("limit") int limit);

    /**
     * 统计日期范围内的总浏览量
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param statsType 统计类型
     * @return Long 总浏览量
     */
    @Query("SELECT SUM(v.viewCount) FROM ViewStats v WHERE v.viewDate BETWEEN :startDate AND :endDate AND v.statsType = :statsType")
    Long sumViewCountByDateRangeAndStatsType(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("statsType") String statsType);
}