package com.puber.blog.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 友链实体类
 *
 * @author wenlishi
 * @version 1.0.0
 * @since 2026-04-13
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "friend_link")
public class FriendLink {

    /**
     * 友链ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 友链名称
     */
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * 友链URL
     */
    @Column(nullable = false, length = 255)
    private String url;

    /**
     * 友链描述
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * 友链Logo/头像URL
     */
    @Column(length = 255)
    private String logo;

    /**
     * 排序顺序（数字越小越靠前）
     */
    @Column(nullable = false)
    private Integer sortOrder;

    /**
     * 状态（ACTIVE=启用/INACTIVE=禁用）
     */
    @Column(nullable = false, length = 20)
    private String status;

    /**
     * 创建时间
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * 创建时间自动设置
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (sortOrder == null) {
            sortOrder = 0;
        }
        if (status == null) {
            status = "ACTIVE";
        }
    }

    /**
     * 更新时间自动设置
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}