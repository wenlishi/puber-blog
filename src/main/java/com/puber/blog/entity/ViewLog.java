package com.puber.blog.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 浏览记录实体类
 * 记录每次文章访问的详细信息
 *
 * @author wenlishi
 * @version 1.0.0
 * @since 2026-04-19
 */
@Entity
@Table(name = "view_log", indexes = {
    @Index(name = "idx_view_log_article_id", columnList = "articleId"),
    @Index(name = "idx_view_log_view_time", columnList = "viewTime")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ViewLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 文章ID
     */
    @Column(nullable = false)
    private Long articleId;

    /**
     * 访问时间
     */
    @Column(nullable = false)
    private LocalDateTime viewTime;

    /**
     * 访问日期（用于统计）
     */
    @Column(nullable = false)
    private java.time.LocalDate viewDate;

    /**
     * 用户IP地址
     */
    @Column(length = 50)
    private String ipAddress;

    /**
     * 用户代理信息
     */
    @Column(columnDefinition = "TEXT")
    private String userAgent;

    /**
     * 用户会话ID（可选）
     */
    @Column(length = 100)
    private String sessionId;
}