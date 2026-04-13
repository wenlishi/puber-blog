package com.puber.blog.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * 评论实体类
 * 对应数据库表：comment
 *
 * @author puber
 * @version 1.0.0
 * @since 2026-04-13
 */
@Entity
@Table(name = "comment")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment extends BaseEntity {

    /**
     * 评论者昵称
     */
    @Column(nullable = false, length = 50)
    private String nickname;

    /**
     * 评论者邮箱
     */
    @Column(nullable = false, length = 100)
    private String email;

    /**
     * 评论者网站
     */
    @Column(length = 255)
    private String website;

    /**
     * 评论内容
     */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    /**
     * 评论状态
     * PENDING: 待审核
     * APPROVED: 已批准
     * REJECTED: 已拒绝
     */
    @Column(nullable = false, length = 20)
    private String status;

    /**
     * IP 地址
     */
    @Column(length = 50)
    private String ipAddress;

    /**
     * 用户代理
     */
    @Column(columnDefinition = "TEXT")
    private String userAgent;

    /**
     * 文章 ID
     */
    @Column(nullable = false)
    private Long articleId;

    /**
     * 父评论 ID
     * 用于多级回复
     */
    @Column
    private Long parentId;

    /**
     * 回复的评论 ID
     */
    @Column
    private Long replyToId;
}