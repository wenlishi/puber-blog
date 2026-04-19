package com.puber.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 后台留言管理视图对象
 * 包含IP地址、UserAgent等详细信息
 *
 * @author wenlishi
 * @version 1.0.0
 * @since 2026-04-19
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminMessageVO {

    /**
     * 留言ID
     */
    private Long id;

    /**
     * 留言者昵称
     */
    private String nickname;

    /**
     * 留言者邮箱
     */
    private String email;

    /**
     * 留言者网站
     */
    private String website;

    /**
     * 留言内容
     */
    private String content;

    /**
     * 留言状态
     */
    private String status;

    /**
     * IP地址
     */
    private String ipAddress;

    /**
     * 用户代理
     */
    private String userAgent;

    /**
     * 管理员回复内容
     */
    private String replyContent;

    /**
     * 管理员回复时间
     */
    private LocalDateTime replyTime;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}