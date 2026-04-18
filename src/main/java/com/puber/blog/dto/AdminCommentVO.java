package com.puber.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 后台评论管理视图对象
 * 包含文章标题等额外信息
 *
 * @author wenlishi
 * @version 1.0.0
 * @since 2026-04-17
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminCommentVO {

    /**
     * 评论ID
     */
    private Long id;

    /**
     * 评论者昵称
     */
    private String nickname;

    /**
     * 评论者邮箱
     */
    private String email;

    /**
     * 评论者网站
     */
    private String website;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 评论状态
     */
    private String status;

    /**
     * 文章ID
     */
    private Long articleId;

    /**
     * 文章标题
     */
    private String articleTitle;

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
}