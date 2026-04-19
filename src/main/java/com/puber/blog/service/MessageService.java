package com.puber.blog.service;

import com.puber.blog.dto.MessageDTO;
import com.puber.blog.dto.MessageVO;
import com.puber.blog.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 留言业务服务接口
 * 提供留言的业务逻辑处理
 *
 * @author wenlishi
 * @version 1.0.0
 * @since 2026-04-19
 */
public interface MessageService {

    /**
     * 创建留言（游客提交）
     * 记录IP地址和UserAgent，XSS过滤内容
     *
     * @param dto 留言DTO
     * @param request HTTP请求对象（用于获取IP和UserAgent）
     * @return Message 创建的留言实体
     */
    Message createMessage(MessageDTO dto, HttpServletRequest request);

    /**
     * 获取已批准留言列表（包含子留言）
     * 用于前台留言板页面展示（树形结构）
     *
     * @return List<MessageVO> 留言列表（树形结构）
     */
    List<MessageVO> getApprovedMessages();

    /**
     * 获取待审核留言列表（分页）
     * 用于后台管理审核
     *
     * @param pageable 分页参数
     * @return Page<Message> 待审核留言分页列表
     */
    Page<Message> getPendingMessages(Pageable pageable);

    /**
     * 获取所有留言列表（分页）
     * 用于后台管理查看所有留言
     *
     * @param pageable 分页参数
     * @return Page<Message> 留言分页列表
     */
    Page<Message> getAllMessages(Pageable pageable);

    /**
     * 批准留言
     * 将留言状态改为APPROVED
     *
     * @param id 留言ID
     */
    void approveMessage(Long id);

    /**
     * 拒绝留言
     * 将留言状态改为REJECTED
     *
     * @param id 留言ID
     */
    void rejectMessage(Long id);

    /**
     * 删除留言
     * 级联删除子留言
     *
     * @param id 留言ID
     */
    void deleteMessage(Long id);

    /**
     * 统计待审核留言数量
     * 用于后台仪表盘显示
     *
     * @return Long 待审核留言数量
     */
    Long countPendingMessages();

    /**
     * 统计已批准留言数量
     *
     * @return Long 已批准留言数量
     */
    Long countApprovedMessages();

    /**
     * 获取最新留言列表
     * 用于前台侧边栏展示
     *
     * @param limit 数量限制
     * @return List<MessageVO> 最新留言列表
     */
    List<MessageVO> getRecentMessages(int limit);

    /**
     * 根据ID获取留言
     *
     * @param id 留言ID
     * @return Message 留言实体
     */
    Message getMessageById(Long id);

    /**
     * 管理员回复留言
     * 添加回复内容并设置回复时间
     *
     * @param id 留言ID
     * @param content 回复内容
     */
    void replyMessage(Long id, String content);
}