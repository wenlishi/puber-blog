package com.puber.blog.repository;

import com.puber.blog.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 留言数据访问层
 * 提供留言的数据库操作方法
 *
 * @author wenlishi
 * @version 1.0.0
 * @since 2026-04-19
 */
@Repository
public interface MessageRepository extends org.springframework.data.jpa.repository.JpaRepository<Message, Long> {

    /**
     * 查询已批准留言（前台展示）
     *
     * @param status 留言状态
     * @return 留言列表
     */
    List<Message> findByStatus(String status);

    /**
     * 按状态分页查询留言（后台管理）
     *
     * @param status 留言状态
     * @param pageable 分页参数
     * @return 分页留言列表
     */
    Page<Message> findByStatus(String status, Pageable pageable);

    /**
     * 查询所有留言（分页）
     *
     * @param pageable 分页参数
     * @return 分页留言列表
     */
    Page<Message> findAll(org.springframework.data.domain.Pageable pageable);

    /**
     * 统计指定状态的留言数量
     *
     * @param status 留言状态
     * @return 留言数量
     */
    Long countByStatus(String status);

    /**
     * 查询最新留言（按创建时间降序）
     *
     * @param status 留言状态
     * @param pageable 分页参数（限制数量）
     * @return 留言列表
     */
    List<Message> findByStatusOrderByCreatedAtDesc(String status, Pageable pageable);

    /**
     * 查询指定父留言下的子留言
     *
     * @param parentId 父留言ID
     * @param status 留言状态
     * @return 子留言列表
     */
    List<Message> findByParentIdAndStatus(Long parentId, String status);
}