package com.puber.blog.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * 浏览统计实体类
 * 存储每日聚合的统计数据
 *
 * @author wenlishi
 * @version 1.0.0
 * @since 2026-04-19
 */
@Entity
@Table(name = "view_stats", indexes = {
    @Index(name = "idx_view_stats_date", columnList = "viewDate"),
    @Index(name = "idx_view_stats_article_id", columnList = "articleId")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ViewStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 统计日期
     */
    @Column(nullable = false)
    private LocalDate viewDate;

    /**
     * 文章ID（如果为null，表示全站统计）
     */
    @Column
    private Long articleId;

    /**
     * 当日浏览量
     */
    @Column(nullable = false)
    private Long viewCount;

    /**
     * 当日独立访客数（根据IP统计）
     */
    @Column
    private Long uniqueVisitors;

    /**
     * 统计类型（SITE: 全站统计, ARTICLE: 单篇文章统计）
     */
    @Column(nullable = false, length = 20)
    private String statsType;
}