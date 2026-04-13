package com.puber.blog.repository;

import com.puber.blog.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 分类 Repository 接口
 * 提供分类数据的 CRUD 操作
 *
 * @author puber
 * @version 1.0.0
 * @since 2026-04-13
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * 根据别名查询分类
     *
     * @param slug 分类别名
     * @return Optional<Category> 分类对象
     */
    Optional<Category> findBySlug(String slug);

    /**
     * 根据名称查询分类
     *
     * @param name 分类名称
     * @return Optional<Category> 分类对象
     */
    Optional<Category> findByName(String name);

    /**
     * 查询所有分类（按排序顺序）
     *
     * @return List<Category> 分类列表
     */
    List<Category> findAllByOrderBySortOrderAsc();

    /**
     * 检查别名是否存在
     *
     * @param slug 分类别名
     * @return true: 存在, false: 不存在
     */
    boolean existsBySlug(String slug);

    /**
     * 检查名称是否存在
     *
     * @param name 分类名称
     * @return true: 存在, false: 不存在
     */
    boolean existsByName(String name);
}