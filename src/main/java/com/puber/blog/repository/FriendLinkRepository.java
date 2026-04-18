package com.puber.blog.repository;

import com.puber.blog.entity.FriendLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 友链数据访问层
 *
 * @author wenlishi
 * @version 1.0.0
 * @since 2026-04-13
 */
@Repository
public interface FriendLinkRepository extends JpaRepository<FriendLink, Long> {

    /**
     * 查询所有启用的友链（按排序顺序）
     *
     * @param status 状态
     * @return List<FriendLink> 友链列表
     */
    List<FriendLink> findByStatusOrderBySortOrderAsc(String status);

    /**
     * 查询所有友链（按排序顺序）
     *
     * @return List<FriendLink> 友链列表
     */
    List<FriendLink> findAllByOrderBySortOrderAsc();

    /**
     * 查询最大排序值
     *
     * @return Integer 最大排序值，如果没有友链则返回null
     */
    @Query("SELECT MAX(f.sortOrder) FROM FriendLink f")
    Integer findMaxSortOrder();
}