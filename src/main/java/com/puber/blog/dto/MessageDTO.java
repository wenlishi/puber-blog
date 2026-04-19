package com.puber.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 留言数据传输对象（DTO）
 * 用于接收前端提交的留言数据
 *
 * @author wenlishi
 * @version 1.0.0
 * @since 2026-04-19
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageDTO {

    /**
     * 留言者昵称
     */
    private String nickname;

    /**
     * 留言者邮箱
     */
    private String email;

    /**
     * 留言者网站（可选）
     */
    private String website;

    /**
     * 留言内容
     */
    private String content;

    /**
     * 父留言ID（用于回复留言）
     */
    private Long parentId;

    /**
     * 回复目标ID（用于回复特定留言者）
     */
    private Long replyToId;
}