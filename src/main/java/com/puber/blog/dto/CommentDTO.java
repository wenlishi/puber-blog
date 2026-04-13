package com.puber.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 评论数据传输对象（DTO）
 * 用于接收前端提交的评论数据
 *
 * @author wenlishi
 * @version 1.0.0
 * @since 2026-04-13
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDTO {

    /**
     * 评论者昵称
     */
    private String nickname;

    /**
     * 评论者邮箱
     */
    private String email;

    /**
     * 评论者网站（可选）
     */
    private String website;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 文章ID
     */
    private Long articleId;

    /**
     * 父评论ID（用于回复评论）
     */
    private Long parentId;

    /**
     * 回复目标ID（用于回复特定评论者）
     */
    private Long replyToId;
}