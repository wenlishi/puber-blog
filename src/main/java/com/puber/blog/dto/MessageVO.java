package com.puber.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 留言视图对象（VO）
 * 用于返回给前端展示的留言数据
 *
 * @author wenlishi
 * @version 1.0.0
 * @since 2026-04-19
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageVO {

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
     * 子留言列表（回复）
     */
    private List<MessageVO> replies;

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