package com.puber.blog.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 留言实体类
 * 对应数据库表：message
 *
 * @author wenlishi
 * @version 1.0.0
 * @since 2026-04-19
 */
@Entity
@Table(name = "message")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message extends BaseEntity {

    /**
     * 留言者昵称
     */
    @Column(nullable = false, length = 50)
    private String nickname;

    /**
     * 留言者邮箱
     */
    @Column(nullable = false, length = 100)
    private String email;

    /**
     * 留言者网站（可选）
     */
    @Column(length = 255)
    private String website;

    /**
     * 留言内容
     */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    /**
     * 留言状态
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
     * 父留言 ID
     * 用于多级回复（树形结构）
     */
    @Column
    private Long parentId;

    /**
     * 回复的目标留言 ID
     * 用于显示"回复某某"
     */
    @Column
    private Long replyToId;

    /**
     * 管理员回复内容
     */
    @Column(columnDefinition = "TEXT")
    private String replyContent;

    /**
     * 管理员回复时间
     */
    @Column
    private LocalDateTime replyTime;
}