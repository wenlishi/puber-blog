package com.puber.blog.service.impl;

import com.puber.blog.annotation.LogRecord;
import com.puber.blog.annotation.LogRecord.LogLevel;
import com.puber.blog.entity.FriendLink;
import com.puber.blog.exception.BusinessException;
import com.puber.blog.repository.FriendLinkRepository;
import com.puber.blog.service.FriendLinkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 友链业务服务实现类
 *
 * @author wenlishi
 * @version 1.0.0
 * @since 2026-04-13
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FriendLinkServiceImpl implements FriendLinkService {

    private final FriendLinkRepository friendLinkRepository;

    /**
     * 获取所有友链（按排序顺序）
     *
     * @return List<FriendLink> 友链列表
     */
    @Override
    public List<FriendLink> getAllFriendLinks() {
        log.debug("获取所有友链");
        return friendLinkRepository.findAllByOrderBySortOrderAsc();
    }

    /**
     * 获取所有启用的友链（按排序顺序）
     *
     * @return List<FriendLink> 启用的友链列表
     */
    @Override
    public List<FriendLink> getActiveFriendLinks() {
        log.debug("获取所有启用的友链");
        return friendLinkRepository.findByStatusOrderBySortOrderAsc("ACTIVE");
    }

    /**
     * 根据ID获取友链
     *
     * @param id 友链ID
     * @return FriendLink 友链实体
     */
    @Override
    public FriendLink getFriendLinkById(Long id) {
        log.debug("根据ID获取友链：{}", id);
        return friendLinkRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "友链不存在：" + id));
    }

    /**
     * 创建友链
     *
     * @param friendLink 友链实体
     * @return FriendLink 创建的友链
     */
    @Override
    @Transactional
    @LogRecord(operation = "添加友链", level = LogLevel.INFO, recordParams = true, recordTime = true)
    public FriendLink createFriendLink(FriendLink friendLink) {
        log.info("创建友链：name={}, url={}", friendLink.getName(), friendLink.getUrl());

        // 设置默认值
        if (friendLink.getSortOrder() == null) {
            // 自动设置为当前最大排序值+1，新友链排在最后
            Integer maxSortOrder = friendLinkRepository.findMaxSortOrder();
            friendLink.setSortOrder(maxSortOrder != null ? maxSortOrder + 1 : 0);
        }
        if (friendLink.getStatus() == null) {
            friendLink.setStatus("ACTIVE");
        }

        return friendLinkRepository.save(friendLink);
    }

    /**
     * 更新友链
     *
     * @param id 友链ID
     * @param friendLink 友链实体
     * @return FriendLink 更新后的友链
     */
    @Override
    @Transactional
    @LogRecord(operation = "编辑友链", level = LogLevel.INFO, recordParams = true, recordTime = true)
    public FriendLink updateFriendLink(Long id, FriendLink friendLink) {
        log.info("更新友链：id={}", id);

        FriendLink existingFriendLink = getFriendLinkById(id);

        // 更新字段
        existingFriendLink.setName(friendLink.getName());
        existingFriendLink.setUrl(friendLink.getUrl());
        existingFriendLink.setDescription(friendLink.getDescription());
        existingFriendLink.setLogo(friendLink.getLogo());
        existingFriendLink.setSortOrder(friendLink.getSortOrder());
        existingFriendLink.setStatus(friendLink.getStatus());

        return friendLinkRepository.save(existingFriendLink);
    }

    /**
     * 删除友链
     *
     * @param id 友链ID
     */
    @Override
    @Transactional
    @LogRecord(operation = "删除友链", level = LogLevel.WARN, recordParams = true, recordTime = true)
    public void deleteFriendLink(Long id) {
        log.info("删除友链：id={}", id);

        FriendLink friendLink = getFriendLinkById(id);
        friendLinkRepository.delete(friendLink);
    }

    /**
     * 启用友链
     *
     * @param id 友链ID
     */
    @Override
    @Transactional
    public void activateFriendLink(Long id) {
        log.info("启用友链：id={}", id);

        FriendLink friendLink = getFriendLinkById(id);
        friendLink.setStatus("ACTIVE");
        friendLinkRepository.save(friendLink);
    }

    /**
     * 禁用友链
     *
     * @param id 友链ID
     */
    @Override
    @Transactional
    public void deactivateFriendLink(Long id) {
        log.info("禁用友链：id={}", id);

        FriendLink friendLink = getFriendLinkById(id);
        friendLink.setStatus("INACTIVE");
        friendLinkRepository.save(friendLink);
    }
}