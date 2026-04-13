package com.puber.blog.service;

import com.puber.blog.entity.FriendLink;
import java.util.List;

/**
 * 友链业务服务接口
 *
 * @author wenlishi
 * @version 1.0.0
 * @since 2026-04-13
 */
public interface FriendLinkService {

    /**
     * 获取所有友链（按排序顺序）
     *
     * @return List<FriendLink> 友链列表
     */
    List<FriendLink> getAllFriendLinks();

    /**
     * 获取所有启用的友链（按排序顺序）
     *
     * @return List<FriendLink> 启用的友链列表
     */
    List<FriendLink> getActiveFriendLinks();

    /**
     * 根据ID获取友链
     *
     * @param id 友链ID
     * @return FriendLink 友链实体
     */
    FriendLink getFriendLinkById(Long id);

    /**
     * 创建友链
     *
     * @param friendLink 友链实体
     * @return FriendLink 创建的友链
     */
    FriendLink createFriendLink(FriendLink friendLink);

    /**
     * 更新友链
     *
     * @param id 友链ID
     * @param friendLink 友链实体
     * @return FriendLink 更新后的友链
     */
    FriendLink updateFriendLink(Long id, FriendLink friendLink);

    /**
     * 删除友链
     *
     * @param id 友链ID
     */
    void deleteFriendLink(Long id);

    /**
     * 启用友链
     *
     * @param id 友链ID
     */
    void activateFriendLink(Long id);

    /**
     * 禁用友链
     *
     * @param id 友链ID
     */
    void deactivateFriendLink(Long id);
}