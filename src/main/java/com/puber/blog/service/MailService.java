package com.puber.blog.service;

import com.puber.blog.dto.CommentDTO;

/**
 * 邮件业务服务接口
 *
 * @author wenlishi
 * @version 1.0.0
 * @since 2026-04-13
 */
public interface MailService {

    /**
     * 发送新评论通知邮件给站长
     *
     * @param commentDTO 评论DTO
     * @param articleTitle 文章标题
     * @return boolean 发送是否成功
     */
    boolean sendNewCommentNotification(CommentDTO commentDTO, String articleTitle);

    /**
     * 发送测试邮件（用于验证配置）
     *
     * @param toEmail 收件邮箱
     * @return boolean 发送是否成功
     */
    boolean sendTestEmail(String toEmail);

    /**
     * 检查邮件配置是否完整
     *
     * @return boolean 配置是否完整
     */
    boolean isMailConfigured();
}