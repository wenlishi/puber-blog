package com.puber.blog.controller.api;

import com.puber.blog.dto.MessageDTO;
import com.puber.blog.dto.MessageVO;
import com.puber.blog.entity.Message;
import com.puber.blog.service.MessageService;
import com.puber.blog.vo.Result;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 前台公开留言控制器
 * 提供游客留言功能的REST API
 *
 * @author wenlishi
 * @version 1.0.0
 * @since 2026-04-19
 */
@Slf4j
@RestController
@RequestMapping("/api/public/messages")
@RequiredArgsConstructor
public class PublicMessageController {

    private final MessageService messageService;

    /**
     * 提交留言（游客）
     *
     * @param dto 留言DTO
     * @param request HTTP请求对象
     * @return Result<Message> 创建的留言
     */
    @PostMapping
    public Result<Message> createMessage(@RequestBody MessageDTO dto, HttpServletRequest request) {
        log.info("游客提交留言：nickname={}", dto.getNickname());

        Message message = messageService.createMessage(dto, request);
        return Result.success(message);
    }

    /**
     * 获取留言列表（树形结构）
     *
     * @return Result<List<MessageVO>> 留言列表（树形结构）
     */
    @GetMapping
    public Result<List<MessageVO>> getMessages() {
        log.info("获取留言列表");

        List<MessageVO> messages = messageService.getApprovedMessages();
        return Result.success(messages);
    }

    /**
     * 获取最新留言列表
     *
     * @param limit 数量限制
     * @return Result<List<MessageVO>> 最新留言列表
     */
    @GetMapping("/recent")
    public Result<List<MessageVO>> getRecentMessages(@RequestParam(defaultValue = "5") int limit) {
        log.info("获取最新留言：limit={}", limit);

        List<MessageVO> messages = messageService.getRecentMessages(limit);
        return Result.success(messages);
    }
}