package com.puber.blog.service.impl;

import com.puber.blog.annotation.LogRecord;
import com.puber.blog.annotation.LogRecord.LogLevel;
import com.puber.blog.dto.MessageDTO;
import com.puber.blog.dto.MessageVO;
import com.puber.blog.entity.Message;
import com.puber.blog.exception.BusinessException;
import com.puber.blog.repository.MessageRepository;
import com.puber.blog.service.MailService;
import com.puber.blog.service.MessageService;
import com.puber.blog.utils.IpUtils;
import com.puber.blog.utils.XssUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 留言业务服务实现类
 * 实现留言的业务逻辑处理
 *
 * @author wenlishi
 * @version 1.0.0
 * @since 2026-04-19
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final MailService mailService;

    /**
     * 创建留言（游客提交）
     *
     * @param dto 留言DTO
     * @param request HTTP请求对象
     * @return Message 创建的留言实体
     */
    @Override
    @Transactional
    @LogRecord(operation = "提交留言", level = LogLevel.INFO, recordParams = true, recordTime = true)
    public Message createMessage(MessageDTO dto, HttpServletRequest request) {

        // 验证必填字段
        if (dto.getNickname() == null || dto.getNickname().trim().isEmpty()) {
            throw new BusinessException(400, "昵称不能为空");
        }
        if (dto.getEmail() == null || dto.getEmail().trim().isEmpty()) {
            throw new BusinessException(400, "邮箱不能为空");
        }
        if (dto.getContent() == null || dto.getContent().trim().isEmpty()) {
            throw new BusinessException(400, "留言内容不能为空");
        }

        // 获取IP地址和UserAgent
        String ipAddress = IpUtils.getIpAddress(request);
        String userAgent = request.getHeader("User-Agent");

        // XSS 过滤：使用严格策略过滤留言内容
        String safeContent = XssUtils.sanitizeStrict(dto.getContent().trim());

        // 创建留言实体
        Message message = Message.builder()
                .nickname(dto.getNickname().trim())
                .email(dto.getEmail().trim())
                .website(dto.getWebsite() != null ? dto.getWebsite().trim() : null)
                .content(safeContent)
                .parentId(dto.getParentId())
                .replyToId(dto.getReplyToId())
                .status("PENDING")  // 默认待审核
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .build();

        // 保存留言
        Message savedMessage = messageRepository.save(message);

        // 发送新留言通知邮件给站长
        try {
            mailService.sendNewMessageNotification(dto);
        } catch (Exception e) {
            // 邮件发送失败不影响留言提交
            log.warn("发送留言通知邮件失败：{}", e.getMessage());
        }

        return savedMessage;
    }

    /**
     * 获取已批准留言列表（树形结构）
     *
     * @return List<MessageVO> 留言列表
     */
    @Override
    public List<MessageVO> getApprovedMessages() {
        log.debug("获取已批准留言列表");

        // 查询所有已批准留言
        List<Message> messages = messageRepository.findByStatus("APPROVED");

        // 构建树形结构
        return buildMessageTree(messages);
    }

    /**
     * 获取待审核留言列表（分页）
     *
     * @param pageable 分页参数
     * @return Page<Message> 待审核留言分页列表
     */
    @Override
    public Page<Message> getPendingMessages(Pageable pageable) {
        log.debug("获取待审核留言列表");

        return messageRepository.findByStatus("PENDING", pageable);
    }

    /**
     * 获取所有留言列表（分页）
     *
     * @param pageable 分页参数
     * @return Page<Message> 留言分页列表
     */
    @Override
    public Page<Message> getAllMessages(Pageable pageable) {
        log.debug("获取所有留言列表");

        return messageRepository.findAll(pageable);
    }

    /**
     * 批准留言
     *
     * @param id 留言ID
     */
    @Override
    @Transactional
    @LogRecord(operation = "批准留言", level = LogLevel.INFO, recordParams = true, recordTime = true)
    public void approveMessage(Long id) {
        Message message = getMessageById(id);
        message.setStatus("APPROVED");
        messageRepository.save(message);
    }

    /**
     * 拒绝留言
     *
     * @param id 留言ID
     */
    @Override
    @Transactional
    @LogRecord(operation = "拒绝留言", level = LogLevel.WARN, recordParams = true, recordTime = true)
    public void rejectMessage(Long id) {
        Message message = getMessageById(id);
        message.setStatus("REJECTED");
        messageRepository.save(message);
    }

    /**
     * 删除留言
     *
     * @param id 留言ID
     */
    @Override
    @Transactional
    @LogRecord(operation = "删除留言", level = LogLevel.WARN, recordParams = true, recordTime = true)
    public void deleteMessage(Long id) {
        log.info("删除留言：id={}", id);

        Message message = getMessageById(id);

        // 删除该留言的所有子留言
        List<Message> childMessages = messageRepository.findByParentIdAndStatus(id, null);
        if (!childMessages.isEmpty()) {
            messageRepository.deleteAll(childMessages);
        }

        // 删除留言本身
        messageRepository.delete(message);
    }

    /**
     * 统计待审核留言数量
     *
     * @return Long 待审核留言数量
     */
    @Override
    public Long countPendingMessages() {
        return messageRepository.countByStatus("PENDING");
    }

    /**
     * 统计已批准留言数量
     *
     * @return Long 已批准留言数量
     */
    @Override
    public Long countApprovedMessages() {
        return messageRepository.countByStatus("APPROVED");
    }

    /**
     * 获取最新留言列表
     *
     * @param limit 数量限制
     * @return List<MessageVO> 最新留言列表
     */
    @Override
    public List<MessageVO> getRecentMessages(int limit) {
        log.debug("获取最新留言：limit={}", limit);

        // 查询最新的已批准留言
        Pageable pageable = Pageable.ofSize(limit);
        List<Message> messages = messageRepository.findByStatusOrderByCreatedAtDesc("APPROVED", pageable);

        return messages.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    /**
     * 根据ID获取留言
     *
     * @param id 留言ID
     * @return Message 留言实体
     */
    @Override
    public Message getMessageById(Long id) {
        return messageRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "留言不存在：" + id));
    }

    /**
     * 管理员回复留言
     * 添加回复内容并设置回复时间
     *
     * @param id 留言ID
     * @param content 回复内容
     */
    @Override
    @Transactional
    @LogRecord(operation = "回复留言", level = LogLevel.INFO, recordParams = true, recordTime = true)
    public void replyMessage(Long id, String content) {
        log.info("管理员回复留言：id={}, content={}", id, content);

        // 验证回复内容
        if (content == null || content.trim().isEmpty()) {
            throw new BusinessException(400, "回复内容不能为空");
        }

        // 获取留言
        Message message = getMessageById(id);

        // XSS过滤回复内容
        String safeContent = XssUtils.sanitizeStrict(content.trim());

        // 设置回复内容和时间
        message.setReplyContent(safeContent);
        message.setReplyTime(LocalDateTime.now());

        // 保存留言
        messageRepository.save(message);
    }

    /**
     * 构建留言树形结构
     * 将留言按照父子关系组织成树形结构
     *
     * @param messages 留言列表
     * @return List<MessageVO> 树形留言列表
     */
    private List<MessageVO> buildMessageTree(List<Message> messages) {
        // 分离出顶级留言（没有父留言的留言）
        List<Message> topLevelMessages = messages.stream()
                .filter(m -> m.getParentId() == null)
                .collect(Collectors.toList());

        // 按父留言ID分组所有子留言
        Map<Long, List<Message>> childMessagesMap = messages.stream()
                .filter(m -> m.getParentId() != null)
                .collect(Collectors.groupingBy(Message::getParentId));

        // 构建树形结构
        return topLevelMessages.stream()
                .map(message -> {
                    MessageVO vo = convertToVO(message);

                    // 添加子留言
                    List<Message> children = childMessagesMap.getOrDefault(message.getId(), new ArrayList<>());
                    List<MessageVO> childVOs = children.stream()
                            .map(child -> {
                                MessageVO childVO = convertToVO(child);

                                // 如果有回复目标ID，设置回复目标昵称
                                if (child.getReplyToId() != null) {
                                    Message replyToMessage = messageRepository.findById(child.getReplyToId()).orElse(null);
                                    if (replyToMessage != null) {
                                        childVO.setReplyToNickname(replyToMessage.getNickname());
                                    }
                                }

                                return childVO;
                            })
                            .collect(Collectors.toList());

                    vo.setReplies(childVOs);
                    return vo;
                })
                .collect(Collectors.toList());
    }

    /**
     * 将Message实体转换为MessageVO
     *
     * @param message 留言实体
     * @return MessageVO 留言视图对象
     */
    private MessageVO convertToVO(Message message) {
        return MessageVO.builder()
                .id(message.getId())
                .nickname(message.getNickname())
                .email(message.getEmail())
                .website(message.getWebsite())
                .content(message.getContent())
                .status(message.getStatus())
                .createdAt(message.getCreatedAt())
                .replyContent(message.getReplyContent())
                .replyTime(message.getReplyTime())
                .build();
    }
}