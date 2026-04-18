package com.puber.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 评论视图对象（VO）
 * 用于返回给前端展示的评论数据
 *
 * @author wenlishi
 * @version 1.0.0
 * @since 2026-04-13
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentVO {

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
     * APPROVED: 已批准
     * PENDING: 待审核
     * REJECTED: 已拒绝
     */
    private String status;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 子评论列表（回复）
     */
    private List<CommentVO> replies;

    /**
     * 回复目标昵称（用于显示"回复某某"）
     */
    private String replyToNickname;

    /**
     * 管理员回复内容
     */
    private String replyContent;

    /**
     * 管理员回复时间
     */
    private LocalDateTime replyTime;
}