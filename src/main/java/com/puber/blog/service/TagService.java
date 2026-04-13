package com.puber.blog.service;

import com.puber.blog.dto.TagDTO;
import com.puber.blog.dto.TagVO;
import com.puber.blog.entity.Tag;

import java.util.List;

/**
 * 标签业务服务接口
 * 提供标签的业务逻辑处理
 *
 * @author puber
 * @version 1.0.0
 * @since 2026-04-13
 */
public interface TagService {

    /**
     * 获取所有标签列表
     * 包含文章数量统计
     *
     * @return List<TagVO> 标签列表
     */
    List<TagVO> getAllTags();

    /**
     * 根据ID获取标签
     *
     * @param id 标签ID
     * @return Tag 标签实体
     */
    Tag getTagById(Long id);

    /**
     * 根据slug获取标签
     *
     * @param slug 标签别名
     * @return Tag 标签实体
     */
    Tag getTagBySlug(String slug);

    /**
     * 创建标签
     * 如果slug为空，则根据name自动生成
     *
     * @param dto 标签DTO
     * @return Tag 创建的标签实体
     */
    Tag createTag(TagDTO dto);

    /**
     * 更新标签
     *
     * @param id 标签ID
     * @param dto 标签DTO
     * @return Tag 更新后的标签实体
     */
    Tag updateTag(Long id, TagDTO dto);

    /**
     * 删除标签
     * 如果标签下有文章，则抛出异常
     *
     * @param id 标签ID
     */
    void deleteTag(Long id);

    /**
     * 检查slug是否已存在
     *
     * @param slug 标签别名
     * @param excludeId 排除的标签ID（用于更新时检查）
     * @return true: 已存在, false: 不存在
     */
    boolean checkSlugExists(String slug, Long excludeId);

    /**
     * 检查标签名称是否已存在
     *
     * @param name 标签名称
     * @param excludeId 排除的标签ID（用于更新时检查）
     * @return true: 已存在, false: 不存在
     */
    boolean checkNameExists(String name, Long excludeId);
}