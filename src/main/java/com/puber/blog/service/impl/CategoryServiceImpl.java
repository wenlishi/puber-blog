package com.puber.blog.service.impl;

import com.puber.blog.dto.CategoryDTO;
import com.puber.blog.dto.CategoryVO;
import com.puber.blog.entity.Category;
import com.puber.blog.exception.BusinessException;
import com.puber.blog.repository.ArticleRepository;
import com.puber.blog.repository.CategoryRepository;
import com.puber.blog.service.CategoryService;
import com.puber.blog.utils.SlugUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 分类业务服务实现类
 * 实现分类的业务逻辑处理
 *
 * @author puber
 * @version 1.0.0
 * @since 2026-04-13
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ArticleRepository articleRepository;

    /**
     * 获取所有分类列表
     * 按sortOrder排序，并包含文章数量统计
     * 使用缓存减少数据库查询频率
     *
     * @return List<CategoryVO> 分类列表
     */
    @Override
    @Cacheable(value = "categories", key = "'allCategories'")
    public List<CategoryVO> getAllCategories() {
        log.debug("获取所有分类列表");

        List<Category> categories = categoryRepository.findAllByOrderBySortOrderAsc();

        return categories.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    /**
     * 根据ID获取分类
     *
     * @param id 分类ID
     * @return Category 分类实体
     */
    @Override
    public Category getCategoryById(Long id) {
        log.debug("根据ID获取分类：{}", id);

        return categoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "分类不存在：" + id));
    }

    /**
     * 根据slug获取分类
     *
     * @param slug 分类别名
     * @return Category 分类实体
     */
    @Override
    public Category getCategoryBySlug(String slug) {
        log.debug("根据slug获取分类：{}", slug);

        return categoryRepository.findBySlug(slug)
                .orElseThrow(() -> new BusinessException(404, "分类不存在：" + slug));
    }

    /**
     * 创建分类
     * 如果slug为空，则根据name自动生成
     * 创建后清除缓存，确保下次查询获取最新数据
     *
     * @param dto 分类DTO
     * @return Category 创建的分类实体
     */
    @Override
    @Transactional
    @CacheEvict(value = "categories", allEntries = true)
    public Category createCategory(CategoryDTO dto) {
        log.info("创建分类：{}", dto.getName());

        // 检查名称是否已存在
        if (checkNameExists(dto.getName(), null)) {
            throw new BusinessException(400, "分类名称已存在：" + dto.getName());
        }

        // 处理slug
        String slug = dto.getSlug();
        if (slug == null || slug.trim().isEmpty()) {
            slug = SlugUtils.toSlug(dto.getName());
        }

        // 检查slug是否已存在
        if (checkSlugExists(slug, null)) {
            // 如果slug冲突，添加时间戳后缀
            slug = SlugUtils.toSlugWithTimestamp(dto.getName());
        }

        // 创建分类实体
        Category category = Category.builder()
                .name(dto.getName())
                .slug(slug)
                .description(dto.getDescription())
                .sortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0)
                .build();

        return categoryRepository.save(category);
    }

    /**
     * 更新分类
     * 更新后清除缓存，确保下次查询获取最新数据
     *
     * @param id 分类ID
     * @param dto 分类DTO
     * @return Category 更新后的分类实体
     */
    @Override
    @Transactional
    @CacheEvict(value = "categories", allEntries = true)
    public Category updateCategory(Long id, CategoryDTO dto) {
        log.info("更新分类：{}", id);

        // 获取现有分类
        Category category = getCategoryById(id);

        // 检查名称是否已存在（排除自己）
        if (checkNameExists(dto.getName(), id)) {
            throw new BusinessException(400, "分类名称已存在：" + dto.getName());
        }

        // 处理slug
        String slug = dto.getSlug();
        if (slug == null || slug.trim().isEmpty()) {
            slug = SlugUtils.toSlug(dto.getName());
        }

        // 检查slug是否已存在（排除自己）
        if (checkSlugExists(slug, id)) {
            // 如果slug冲突，添加时间戳后缀
            slug = SlugUtils.toSlugWithTimestamp(dto.getName());
        }

        // 更新分类属性
        category.setName(dto.getName());
        category.setSlug(slug);
        category.setDescription(dto.getDescription());
        category.setSortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0);

        return categoryRepository.save(category);
    }

    /**
     * 删除分类
     * 如果分类下有文章，则抛出异常
     * 删除后清除缓存，确保下次查询获取最新数据
     *
     * @param id 分类ID
     */
    @Override
    @Transactional
    @CacheEvict(value = "categories", allEntries = true)
    public void deleteCategory(Long id) {
        log.info("删除分类：{}", id);

        // 获取分类
        Category category = getCategoryById(id);

        // 检查是否有关联文章
        Long articleCount = articleRepository.countByCategoryIdAndStatus(id, "PUBLISHED");
        if (articleCount > 0) {
            throw new BusinessException(400, "该分类下有" + articleCount + "篇文章，无法删除");
        }

        categoryRepository.delete(category);
    }

    /**
     * 检查slug是否已存在
     *
     * @param slug 分类别名
     * @param excludeId 排除的分类ID（用于更新时检查）
     * @return true: 已存在, false: 不存在
     */
    @Override
    public boolean checkSlugExists(String slug, Long excludeId) {
        if (slug == null || slug.trim().isEmpty()) {
            return false;
        }

        Category existingCategory = categoryRepository.findBySlug(slug).orElse(null);

        if (existingCategory == null) {
            return false;
        }

        // 如果excludeId不为空，且找到的分类ID等于excludeId，则不算冲突
        return excludeId == null || !existingCategory.getId().equals(excludeId);
    }

    /**
     * 检查分类名称是否已存在
     *
     * @param name 分类名称
     * @param excludeId 排除的分类ID（用于更新时检查）
     * @return true: 已存在, false: 不存在
     */
    @Override
    public boolean checkNameExists(String name, Long excludeId) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }

        Category existingCategory = categoryRepository.findByName(name).orElse(null);

        if (existingCategory == null) {
            return false;
        }

        // 如果excludeId不为空，且找到的分类ID等于excludeId，则不算冲突
        return excludeId == null || !existingCategory.getId().equals(excludeId);
    }

    /**
     * 将Category实体转换为CategoryVO
     *
     * @param category 分类实体
     * @return CategoryVO 分类视图对象
     */
    private CategoryVO convertToVO(Category category) {
        // 统计该分类下的文章数量
        Long articleCount = articleRepository.countByCategoryIdAndStatus(category.getId(), "PUBLISHED");

        return CategoryVO.builder()
                .id(category.getId())
                .name(category.getName())
                .slug(category.getSlug())
                .description(category.getDescription())
                .sortOrder(category.getSortOrder())
                .articleCount(articleCount)
                .createdAt(category.getCreatedAt())
                .build();
    }
}