package com.puber.blog.repository;

import com.puber.blog.entity.Demo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 演示 Repository 接口
 *
 * @author puber
 * @version 1.0.0
 * @since 2026-04-16
 */
@Repository
public interface DemoRepository extends JpaRepository<Demo, Long> {

    /**
     * 根据slug查询演示
     *
     * @param slug 演示别名
     * @return Optional<Demo> 演示对象
     */
    Optional<Demo> findBySlug(String slug);

    /**
     * 根据状态查询演示列表（分页）
     *
     * @param status 状态
     * @param pageable 分页参数
     * @return Page<Demo> 演示分页列表
     */
    Page<Demo> findByStatus(String status, Pageable pageable);

    /**
     * 查询所有已发布演示（按发布时间倒序）
     *
     * @param status 状态
     * @param pageable 分页参数
     * @return Page<Demo> 演示分页列表
     */
    @Query("SELECT d FROM Demo d WHERE d.status = :status ORDER BY d.publishedAt DESC")
    Page<Demo> findAllPublished(@Param("status") String status, Pageable pageable);

    /**
     * 检查slug是否存在
     *
     * @param slug 演示别名
     * @return boolean 是否存在
     */
    boolean existsBySlug(String slug);

    /**
     * 统计演示数量
     *
     * @param status 状态
     * @return Long 演示数量
     */
    Long countByStatus(String status);
}