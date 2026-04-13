package com.puber.blog.controller.admin;

import com.puber.blog.dto.TagDTO;
import com.puber.blog.dto.TagVO;
import com.puber.blog.entity.Tag;
import com.puber.blog.service.TagService;
import com.puber.blog.vo.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 后台标签管理控制器
 * 提供标签管理的REST API
 *
 * @author puber
 * @version 1.0.0
 * @since 2026-04-13
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/tags")
@RequiredArgsConstructor
public class AdminTagController {

    private final TagService tagService;

    /**
     * 获取标签列表
     *
     * @return Result<List<TagVO>> 标签列表
     */
    @GetMapping
    public Result<List<TagVO>> getAllTags() {
        log.info("获取标签列表");
        List<TagVO> tags = tagService.getAllTags();
        return Result.success(tags);
    }

    /**
     * 获取标签详情
     *
     * @param id 标签ID
     * @return Result<Tag> 标签详情
     */
    @GetMapping("/{id}")
    public Result<Tag> getTagById(@PathVariable Long id) {
        log.info("获取标签详情：{}", id);
        Tag tag = tagService.getTagById(id);
        return Result.success(tag);
    }

    /**
     * 创建标签
     *
     * @param dto 标签DTO
     * @return Result<Tag> 创建的标签
     */
    @PostMapping
    public Result<Tag> createTag(@RequestBody TagDTO dto) {
        log.info("创建标签：{}", dto.getName());
        Tag tag = tagService.createTag(dto);
        return Result.success(tag);
    }

    /**
     * 更新标签
     *
     * @param id 标签ID
     * @param dto 标签DTO
     * @return Result<Tag> 更新后的标签
     */
    @PutMapping("/{id}")
    public Result<Tag> updateTag(@PathVariable Long id, @RequestBody TagDTO dto) {
        log.info("更新标签：{}", id);
        Tag tag = tagService.updateTag(id, dto);
        return Result.success(tag);
    }

    /**
     * 删除标签
     *
     * @param id 标签ID
     * @return Result<Void> 删除结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteTag(@PathVariable Long id) {
        log.info("删除标签：{}", id);
        tagService.deleteTag(id);
        return Result.success();
    }

    /**
     * 检查slug是否可用
     *
     * @param slug 标签别名
     * @param excludeId 排除的标签ID
     * @return Result<Boolean> true: 已存在, false: 可用
     */
    @GetMapping("/check-slug")
    public Result<Boolean> checkSlugExists(@RequestParam String slug,
                                           @RequestParam(required = false) Long excludeId) {
        log.info("检查slug是否存在：{}, excludeId: {}", slug, excludeId);
        boolean exists = tagService.checkSlugExists(slug, excludeId);
        return Result.success(exists);
    }
}