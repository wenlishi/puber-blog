package com.puber.blog.controller.admin;

import com.puber.blog.dto.AdminMessageVO;
import com.puber.blog.entity.Message;
import com.puber.blog.service.MessageService;
import com.puber.blog.vo.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 后台留言管理控制器
 * 提供留言审核和管理的REST API
 *
 * @author wenlishi
 * @version 1.0.0
 * @since 2026-04-19
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/messages")
@RequiredArgsConstructor
public class AdminMessageController {

    private final MessageService messageService;

    /**
     * 获取所有留言列表（分页）
     *
     * @param page 页码（从0开始）
     * @param size 每页数量
     * @return Result<Page<AdminMessageVO>> 留言分页列表
     */
    @GetMapping
    public Result<Page<AdminMessageVO>> getAllMessages(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("获取所有留言列表：page={}, size={}", page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Message> messages = messageService.getAllMessages(pageable);

        Page<AdminMessageVO> messageVOs = messages.map(this::convertToAdminMessageVO);

        return Result.success(messageVOs);
    }

    /**
     * 获取待审核留言列表（分页）
     *
     * @param page 页码
     * @param size 每页数量
     * @return Result<Page<AdminMessageVO>> 待审核留言分页列表
     */
    @GetMapping("/pending")
    public Result<Page<AdminMessageVO>> getPendingMessages(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("获取待审核留言列表：page={}, size={}", page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Message> messages = messageService.getPendingMessages(pageable);

        Page<AdminMessageVO> messageVOs = messages.map(this::convertToAdminMessageVO);

        return Result.success(messageVOs);
    }

    /**
     * 批准留言
     *
     * @param id 留言ID
     * @return Result<Void> 操作结果
     */
    @PutMapping("/{id}/approve")
    public Result<Void> approveMessage(@PathVariable Long id) {
        log.info("批准留言：id={}", id);

        messageService.approveMessage(id);
        return Result.success();
    }

    /**
     * 拒绝留言
     *
     * @param id 留言ID
     * @return Result<Void> 操作结果
     */
    @PutMapping("/{id}/reject")
    public Result<Void> rejectMessage(@PathVariable Long id) {
        log.info("拒绝留言：id={}", id);

        messageService.rejectMessage(id);
        return Result.success();
    }

    /**
     * 删除留言
     *
     * @param id 留言ID
     * @return Result<Void> 操作结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteMessage(@PathVariable Long id) {
        log.info("删除留言：id={}", id);

        messageService.deleteMessage(id);
        return Result.success();
    }

    /**
     * 统计待审核留言数量
     *
     * @return Result<Long> 待审核留言数量
     */
    @GetMapping("/count/pending")
    public Result<Long> countPendingMessages() {
        log.info("统计待审核留言数量");

        Long count = messageService.countPendingMessages();
        return Result.success(count);
    }

    /**
     * 批量批准留言
     *
     * @param ids 留言ID列表
     * @return Result<Void> 操作结果
     */
    @PutMapping("/batch-approve")
    public Result<Void> batchApproveMessages(@RequestBody List<Long> ids) {
        log.info("批量批准留言：ids={}", ids);

        for (Long id : ids) {
            messageService.approveMessage(id);
        }

        return Result.success();
    }

    /**
     * 批量拒绝留言
     *
     * @param ids 留言ID列表
     * @return Result<Void> 操作结果
     */
    @PutMapping("/batch-reject")
    public Result<Void> batchRejectMessages(@RequestBody List<Long> ids) {
        log.info("批量拒绝留言：ids={}", ids);

        for (Long id : ids) {
            messageService.rejectMessage(id);
        }

        return Result.success();
    }

    /**
     * 管理员回复留言
     *
     * @param id 留言ID
     * @param requestBody 回复内容请求体
     * @return Result<Void> 操作结果
     */
    @PostMapping("/{id}/reply")
    public Result<Void> replyMessage(@PathVariable Long id, @RequestBody Map<String, String> requestBody) {
        log.info("管理员回复留言：id={}", id);

        String content = requestBody.get("content");
        if (content == null || content.trim().isEmpty()) {
            return Result.error(400, "回复内容不能为空");
        }

        messageService.replyMessage(id, content);
        return Result.success();
    }

    /**
     * 将Message实体转换为AdminMessageVO
     *
     * @param message 留言实体
     * @return AdminMessageVO 后台留言视图对象
     */
    private AdminMessageVO convertToAdminMessageVO(Message message) {
        return AdminMessageVO.builder()
                .id(message.getId())
                .nickname(message.getNickname())
                .email(message.getEmail())
                .website(message.getWebsite())
                .content(message.getContent())
                .status(message.getStatus())
                .ipAddress(message.getIpAddress())
                .userAgent(message.getUserAgent())
                .replyContent(message.getReplyContent())
                .replyTime(message.getReplyTime())
                .createdAt(message.getCreatedAt())
                .updatedAt(message.getUpdatedAt())
                .build();
    }
}