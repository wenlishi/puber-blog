package com.puber.blog.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 交互式演示实体类
 * 对应数据库表：demo
 * 存储完整的HTML/CSS/JS内容，不经过XSS过滤
 *
 * @author puber
 * @version 1.0.0
 * @since 2026-04-16
 */
@Entity
@Table(name = "demo")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Demo extends BaseEntity {

    /**
     * 演示名称
     */
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * 演示别名（URL友好，唯一）
     */
    @Column(nullable = false, unique = true, length = 100)
    private String slug;

    /**
     * 演示描述
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * 完整HTML内容
     * 包含HTML、CSS、JavaScript的完整HTML文件
     * TEXT类型，不经过XSS过滤（管理员权限）
     */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String fullHtmlContent;

    /**
     * 状态（PUBLISHED/DRAFT）
     */
    @Column(nullable = false, length = 20)
    private String status;

    /**
     * 浏览次数
     */
    @Column(nullable = false)
    private Long viewCount;

    /**
     * 作者ID
     */
    @Column(nullable = false)
    private Long authorId;

    /**
     * 发布时间
     */
    @Column
    private LocalDateTime publishedAt;
}