package com.puber.blog.repository;

import com.puber.blog.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 标签 Repository 接口
 * 提供标签数据的 CRUD 操作
 *
 * @author puber
 * @version 1.0.0
 * @since 2026-04-13
 */
@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    /**
     * 根据别名查询标签
     *
     * @param slug 标签别名
     * @return Optional<Tag> 标签对象
     */
    Optional<Tag> findBySlug(String slug);

    /**
     * 根据名称查询标签
     *
     * @param name 标签名称
     * @return Optional<Tag> 标签对象
     */
    Optional<Tag> findByName(String name);

    /**
     * 查询所有标签
     *
     * @return List<Tag> 标签列表
     */
    List<Tag> findAll();

    /**
     * 检查别名是否存在
     *
     * @param slug 标签别名
     * @return true: 存在, false: 不存在
     */
    boolean existsBySlug(String slug);

    /**
     * 检查名称是否存在
     *
     * @param name 标签名称
     * @return true: 存在, false: 不存在
     */
    boolean existsByName(String name);
}