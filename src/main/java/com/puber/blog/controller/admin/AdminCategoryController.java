package com.puber.blog.controller.admin;

import com.puber.blog.dto.CategoryDTO;
import com.puber.blog.dto.CategoryVO;
import com.puber.blog.entity.Category;
import com.puber.blog.service.CategoryService;
import com.puber.blog.vo.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 后台分类管理控制器
 * 提供分类管理的REST API
 *
 * @author puber
 * @version 1.0.0
 * @since 2026-04-13
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryController {

    private final CategoryService categoryService;

    /**
     * 获取分类列表
     *
     * @return Result<List<CategoryVO>> 分类列表
     */
    @GetMapping
    public Result<List<CategoryVO>> getAllCategories() {
        log.info("获取分类列表");
        List<CategoryVO> categories = categoryService.getAllCategories();
        return Result.success(categories);
    }

    /**
     * 获取分类详情
     *
     * @param id 分类ID
     * @return Result<Category> 分类详情
     */
    @GetMapping("/{id}")
    public Result<Category> getCategoryById(@PathVariable Long id) {
        log.info("获取分类详情：{}", id);
        Category category = categoryService.getCategoryById(id);
        return Result.success(category);
    }

    /**
     * 创建分类
     *
     * @param dto 分类DTO
     * @return Result<Category> 创建的分类
     */
    @PostMapping
    public Result<Category> createCategory(@RequestBody CategoryDTO dto) {
        log.info("创建分类：{}", dto.getName());
        Category category = categoryService.createCategory(dto);
        return Result.success(category);
    }

    /**
     * 更新分类
     *
     * @param id 分类ID
     * @param dto 分类DTO
     * @return Result<Category> 更新后的分类
     */
    @PutMapping("/{id}")
    public Result<Category> updateCategory(@PathVariable Long id, @RequestBody CategoryDTO dto) {
        log.info("更新分类：{}", id);
        Category category = categoryService.updateCategory(id, dto);
        return Result.success(category);
    }

    /**
     * 删除分类
     *
     * @param id 分类ID
     * @return Result<Void> 删除结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteCategory(@PathVariable Long id) {
        log.info("删除分类：{}", id);
        categoryService.deleteCategory(id);
        return Result.success();
    }

    /**
     * 检查slug是否可用
     *
     * @param slug 分类别名
     * @param excludeId 排除的分类ID
     * @return Result<Boolean> true: 已存在, false: 可用
     */
    @GetMapping("/check-slug")
    public Result<Boolean> checkSlugExists(@RequestParam String slug,
                                           @RequestParam(required = false) Long excludeId) {
        log.info("检查slug是否存在：{}, excludeId: {}", slug, excludeId);
        boolean exists = categoryService.checkSlugExists(slug, excludeId);
        return Result.success(exists);
    }
}