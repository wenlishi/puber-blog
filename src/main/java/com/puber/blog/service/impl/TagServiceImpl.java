package com.puber.blog.service.impl;

import com.puber.blog.dto.TagDTO;
import com.puber.blog.dto.TagVO;
import com.puber.blog.entity.Tag;
import com.puber.blog.exception.BusinessException;
import com.puber.blog.repository.ArticleTagRepository;
import com.puber.blog.repository.TagRepository;
import com.puber.blog.service.TagService;
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
 * 标签业务服务实现类
 * 实现标签的业务逻辑处理
 *
 * @author puber
 * @version 1.0.0
 * @since 2026-04-13
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;
    private final ArticleTagRepository articleTagRepository;

    /**
     * 获取所有标签列表
     * 包含文章数量统计
     * 使用缓存减少数据库查询频率
     *
     * @return List<TagVO> 标签列表
     */
    @Override
    @Cacheable(value = "tags", key = "'allTags'")
    public List<TagVO> getAllTags() {
        log.debug("获取所有标签列表");

        List<Tag> tags = tagRepository.findAll();

        return tags.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    /**
     * 根据ID获取标签
     *
     * @param id 标签ID
     * @return Tag 标签实体
     */
    @Override
    public Tag getTagById(Long id) {
        log.debug("根据ID获取标签：{}", id);

        return tagRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "标签不存在：" + id));
    }

    /**
     * 根据slug获取标签
     *
     * @param slug 标签别名
     * @return Tag 标签实体
     */
    @Override
    public Tag getTagBySlug(String slug) {
        log.debug("根据slug获取标签：{}", slug);

        return tagRepository.findBySlug(slug)
                .orElseThrow(() -> new BusinessException(404, "标签不存在：" + slug));
    }

    /**
     * 创建标签
     * 如果slug为空，则根据name自动生成
     * 创建后清除缓存，确保下次查询获取最新数据
     *
     * @param dto 标签DTO
     * @return Tag 创建的标签实体
     */
    @Override
    @Transactional
    @CacheEvict(value = "tags", allEntries = true)
    public Tag createTag(TagDTO dto) {
        log.info("创建标签：{}", dto.getName());

        // 检查名称是否已存在
        if (checkNameExists(dto.getName(), null)) {
            throw new BusinessException(400, "标签名称已存在：" + dto.getName());
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

        // 创建标签实体
        Tag tag = Tag.builder()
                .name(dto.getName())
                .slug(slug)
                .build();

        return tagRepository.save(tag);
    }

    /**
     * 更新标签
     * 更新后清除缓存，确保下次查询获取最新数据
     *
     * @param id 标签ID
     * @param dto 标签DTO
     * @return Tag 更新后的标签实体
     */
    @Override
    @Transactional
    @CacheEvict(value = "tags", allEntries = true)
    public Tag updateTag(Long id, TagDTO dto) {
        log.info("更新标签：{}", id);

        // 获取现有标签
        Tag tag = getTagById(id);

        // 检查名称是否已存在（排除自己）
        if (checkNameExists(dto.getName(), id)) {
            throw new BusinessException(400, "标签名称已存在：" + dto.getName());
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

        // 更新标签属性
        tag.setName(dto.getName());
        tag.setSlug(slug);

        return tagRepository.save(tag);
    }

    /**
     * 删除标签
     * 如果标签下有文章，则抛出异常
     * 删除后清除缓存，确保下次查询获取最新数据
     *
     * @param id 标签ID
     */
    @Override
    @Transactional
    @CacheEvict(value = "tags", allEntries = true)
    public void deleteTag(Long id) {
        log.info("删除标签：{}", id);

        // 获取标签
        Tag tag = getTagById(id);

        // 检查是否有关联文章
        Long articleCount = articleTagRepository.countByTagId(id);
        if (articleCount > 0) {
            throw new BusinessException(400, "该标签下有" + articleCount + "篇文章，无法删除");
        }

        tagRepository.delete(tag);
    }

    /**
     * 检查slug是否已存在
     *
     * @param slug 标签别名
     * @param excludeId 排除的标签ID（用于更新时检查）
     * @return true: 已存在, false: 不存在
     */
    @Override
    public boolean checkSlugExists(String slug, Long excludeId) {
        if (slug == null || slug.trim().isEmpty()) {
            return false;
        }

        Tag existingTag = tagRepository.findBySlug(slug).orElse(null);

        if (existingTag == null) {
            return false;
        }

        // 如果excludeId不为空，且找到的标签ID等于excludeId，则不算冲突
        return excludeId == null || !existingTag.getId().equals(excludeId);
    }

    /**
     * 检查标签名称是否已存在
     *
     * @param name 标签名称
     * @param excludeId 排除的标签ID（用于更新时检查）
     * @return true: 已存在, false: 不存在
     */
    @Override
    public boolean checkNameExists(String name, Long excludeId) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }

        Tag existingTag = tagRepository.findByName(name).orElse(null);

        if (existingTag == null) {
            return false;
        }

        // 如果excludeId不为空，且找到的标签ID等于excludeId，则不算冲突
        return excludeId == null || !existingTag.getId().equals(excludeId);
    }

    /**
     * 将Tag实体转换为TagVO
     *
     * @param tag 标签实体
     * @return TagVO 标签视图对象
     */
    private TagVO convertToVO(Tag tag) {
        // 统计该标签下的文章数量
        Long articleCount = articleTagRepository.countByTagId(tag.getId());

        return TagVO.builder()
                .id(tag.getId())
                .name(tag.getName())
                .slug(tag.getSlug())
                .articleCount(articleCount)
                .createdAt(tag.getCreatedAt())
                .build();
    }
}