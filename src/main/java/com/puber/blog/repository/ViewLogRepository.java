package com.puber.blog.repository;

import com.puber.blog.entity.ViewLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * 浏览记录 Repository 接口
 *
 * @author wenlishi
 * @version 1.0.0
 * @since 2026-04-19
 */
@Repository
public interface ViewLogRepository extends JpaRepository<ViewLog, Long> {

    /**
     * 根据日期查询浏览记录
     *
     * @param viewDate 访问日期
     * @return List<ViewLog> 浏览记录列表
     */
    List<ViewLog> findByViewDate(LocalDate viewDate);

    /**
     * 根据日期范围查询浏览记录
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return List<ViewLog> 浏览记录列表
     */
    List<ViewLog> findByViewDateBetween(LocalDate startDate, LocalDate endDate);

    /**
     * 统计指定日期的浏览量
     *
     * @param viewDate 访问日期
     * @return Long 浏览量
     */
    @Query("SELECT COUNT(v) FROM ViewLog v WHERE v.viewDate = :viewDate")
    Long countByViewDate(@Param("viewDate") LocalDate viewDate);

    /**
     * 统计指定日期的独立访客数（根据IP）
     *
     * @param viewDate 访问日期
     * @return Long 独立访客数
     */
    @Query("SELECT COUNT(DISTINCT v.ipAddress) FROM ViewLog v WHERE v.viewDate = :viewDate")
    Long countUniqueVisitorsByViewDate(@Param("viewDate") LocalDate viewDate);

    /**
     * 统计指定日期指定文章的浏览量
     *
     * @param viewDate 访问日期
     * @param articleId 文章ID
     * @return Long 浏览量
     */
    @Query("SELECT COUNT(v) FROM ViewLog v WHERE v.viewDate = :viewDate AND v.articleId = :articleId")
    Long countByViewDateAndArticleId(@Param("viewDate") LocalDate viewDate, @Param("articleId") Long articleId);
}