package com.puber.blog.service;

import com.puber.blog.dto.CategoryDTO;
import com.puber.blog.dto.CategoryVO;
import com.puber.blog.entity.Category;

import java.util.List;

/**
 * 分类业务服务接口
 * 提供分类的业务逻辑处理
 *
 * @author puber
 * @version 1.0.0
 * @since 2026-04-13
 */
public interface CategoryService {

    /**
     * 获取所有分类列表
     * 按sortOrder排序，并包含文章数量统计
     *
     * @return List<CategoryVO> 分类列表
     */
    List<CategoryVO> getAllCategories();

    /**
     * 根据ID获取分类
     *
     * @param id 分类ID
     * @return Category 分类实体
     */
    Category getCategoryById(Long id);

    /**
     * 根据slug获取分类
     *
     * @param slug 分类别名
     * @return Category 分类实体
     */
    Category getCategoryBySlug(String slug);

    /**
     * 创建分类
     * 如果slug为空，则根据name自动生成
     *
     * @param dto 分类DTO
     * @return Category 创建的分类实体
     */
    Category createCategory(CategoryDTO dto);

    /**
     * 更新分类
     *
     * @param id 分类ID
     * @param dto 分类DTO
     * @return Category 更新后的分类实体
     */
    Category updateCategory(Long id, CategoryDTO dto);

    /**
     * 删除分类
     * 如果分类下有文章，则抛出异常
     *
     * @param id 分类ID
     */
    void deleteCategory(Long id);

    /**
     * 检查slug是否已存在
     *
     * @param slug 分类别名
     * @param excludeId 排除的分类ID（用于更新时检查）
     * @return true: 已存在, false: 不存在
     */
    boolean checkSlugExists(String slug, Long excludeId);

    /**
     * 检查分类名称是否已存在
     *
     * @param name 分类名称
     * @param excludeId 排除的分类ID（用于更新时检查）
     * @return true: 已存在, false: 不存在
     */
    boolean checkNameExists(String name, Long excludeId);
}