package com.puber.blog.service.impl;

import com.puber.blog.dto.CommentDTO;
import com.puber.blog.service.MailService;
import com.puber.blog.service.SiteSettingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Properties;

/**
 * 邮件业务服务实现类
 *
 * @author wenlishi
 * @version 1.0.0
 * @since 2026-04-13
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    private final SiteSettingService siteSettingService;

    /**
     * 发送新评论通知邮件给站长（异步方式）
     * 使用独立线程池执行，不阻塞评论提交流程
     *
     * @param commentDTO 评论DTO
     * @param articleTitle 文章标题
     */
    @Override
    @Async("mailTaskExecutor")
    public void sendNewCommentNotification(CommentDTO commentDTO, String articleTitle) {
        log.info("异步发送新评论通知邮件：articleTitle={}, nickname={}", articleTitle, commentDTO.getNickname());

        if (!isMailConfigured()) {
            log.warn("邮件配置不完整，跳过发送");
            return;
        }

        try {
            // 创建邮件发送器
            JavaMailSender mailSender = createMailSender();

            // 获取站长邮箱
            String adminEmail = siteSettingService.getSettingValue("mail_admin_email");
            if (adminEmail == null || adminEmail.isEmpty()) {
                log.warn("未配置站长邮箱，跳过发送");
                return;
            }

            // 创建邮件消息
            SimpleMailMessage message = new SimpleMailMessage();

            // QQ邮箱要求：发件人地址必须和授权用户一致
            String smtpUsername = siteSettingService.getSettingValue("mail_smtp_username");
            message.setFrom(smtpUsername);  // 设置发件人地址

            message.setTo(adminEmail);
            message.setSubject("【博客系统】新评论待审核 - " + articleTitle);
            message.setText(buildCommentNotificationText(commentDTO, articleTitle));

            // 发送邮件
            mailSender.send(message);

            log.info("新评论通知邮件发送成功：to={}", adminEmail);
        } catch (Exception e) {
            log.error("发送新评论通知邮件失败", e);
        }
    }

    /**
     * 发送测试邮件（用于验证配置）
     *
     * @param toEmail 收件邮箱
     * @return boolean 发送是否成功
     */
    @Override
    public boolean sendTestEmail(String toEmail) {
        log.info("发送测试邮件：toEmail={}", toEmail);

        if (!isMailConfigured()) {
            log.warn("邮件配置不完整，无法发送测试邮件");
            return false;
        }

        try {
            // 创建邮件发送器
            JavaMailSender mailSender = createMailSender();

            // 创建测试邮件
            SimpleMailMessage message = new SimpleMailMessage();

            // QQ邮箱要求：发件人地址必须和授权用户一致
            String smtpUsername = siteSettingService.getSettingValue("mail_smtp_username");
            message.setFrom(smtpUsername);  // 设置发件人地址

            message.setTo(toEmail);
            message.setSubject("【博客系统】邮件配置测试");
            message.setText("这是一封测试邮件，用于验证SMTP邮件配置是否正确。\n\n如果您收到这封邮件，说明邮件配置成功！\n\n发送时间：" + java.time.LocalDateTime.now());

            // 发送邮件
            mailSender.send(message);

            log.info("测试邮件发送成功：to={}", toEmail);
            return true;
        } catch (Exception e) {
            log.error("发送测试邮件失败", e);
            return false;
        }
    }

    /**
     * 检查邮件配置是否完整
     *
     * @return boolean 配置是否完整
     */
    @Override
    public boolean isMailConfigured() {
        String smtpHost = siteSettingService.getSettingValue("mail_smtp_host");
        String smtpPort = siteSettingService.getSettingValue("mail_smtp_port");
        String smtpUsername = siteSettingService.getSettingValue("mail_smtp_username");
        String smtpPassword = siteSettingService.getSettingValue("mail_smtp_password");
        String adminEmail = siteSettingService.getSettingValue("mail_admin_email");
        String mailEnabled = siteSettingService.getSettingValue("mail_enabled");

        // 检查必要配置是否存在
        return smtpHost != null && !smtpHost.isEmpty()
                && smtpPort != null && !smtpPort.isEmpty()
                && smtpUsername != null && !smtpUsername.isEmpty()
                && smtpPassword != null && !smtpPassword.isEmpty()
                && adminEmail != null && !adminEmail.isEmpty()
                && "true".equals(mailEnabled);
    }

    /**
     * 创建邮件发送器
     * 根据端口自动选择SSL或STARTTLS加密方式
     *
     * @return JavaMailSender 邮件发送器
     */
    private JavaMailSender createMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        // 从系统配置获取SMTP参数
        String host = siteSettingService.getSettingValue("mail_smtp_host");
        int port = Integer.parseInt(siteSettingService.getSettingValue("mail_smtp_port"));
        String username = siteSettingService.getSettingValue("mail_smtp_username");
        String password = siteSettingService.getSettingValue("mail_smtp_password");

        mailSender.setHost(host);
        mailSender.setPort(port);
        mailSender.setUsername(username);
        mailSender.setPassword(password);

        // 配置邮件属性（根据端口自动选择加密方式）
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.debug", "false");

        // 465端口使用SSL加密，587端口使用STARTTLS加密
        if (port == 465 || port == 994) {
            // SSL加密方式（适用于QQ邮箱465端口）
            props.put("mail.smtp.ssl.enable", "true");
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.socketFactory.port", String.valueOf(port));
            log.debug("使用SSL加密方式连接SMTP服务器：port={}", port);
        } else if (port == 587 || port == 25) {
            // STARTTLS加密方式
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.starttls.required", "true");
            log.debug("使用STARTTLS加密方式连接SMTP服务器：port={}", port);
        } else {
            // 其他端口默认尝试SSL
            props.put("mail.smtp.ssl.enable", "true");
            log.warn("非标准SMTP端口{}，默认使用SSL加密方式", port);
        }

        return mailSender;
    }

    /**
     * 构建评论通知邮件内容
     *
     * @param commentDTO 评论DTO
     * @param articleTitle 文章标题
     * @return String 邮件内容
     */
    private String buildCommentNotificationText(CommentDTO commentDTO, String articleTitle) {
        StringBuilder content = new StringBuilder();
        content.append("您的博客收到一条新评论，需要审核。\n\n");
        content.append("文章标题：").append(articleTitle).append("\n\n");
        content.append("评论详情：\n");
        content.append("━━━━━━━━━━━━━━━━━━━━━━━━\n");
        content.append("评论者：").append(commentDTO.getNickname()).append("\n");
        content.append("邮箱：").append(commentDTO.getEmail()).append("\n");
        if (commentDTO.getWebsite() != null && !commentDTO.getWebsite().isEmpty()) {
            content.append("网站：").append(commentDTO.getWebsite()).append("\n");
        }
        content.append("内容：").append(commentDTO.getContent()).append("\n");
        content.append("━━━━━━━━━━━━━━━━━━━━━━━━\n\n");
        content.append("请登录后台管理系统审核该评论。\n");
        content.append("后台地址：").append(siteSettingService.getSettingValue("site_url")).append("/admin/comments\n\n");
        content.append("此邮件由系统自动发送，请勿回复。\n");

        return content.toString();
    }
}