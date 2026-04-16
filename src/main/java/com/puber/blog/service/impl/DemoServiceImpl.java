package com.puber.blog.service.impl;

import com.puber.blog.annotation.LogRecord;
import com.puber.blog.annotation.LogRecord.LogLevel;
import com.puber.blog.dto.DemoDTO;
import com.puber.blog.dto.DemoVO;
import com.puber.blog.entity.Demo;
import com.puber.blog.entity.User;
import com.puber.blog.exception.BusinessException;
import com.puber.blog.repository.DemoRepository;
import com.puber.blog.repository.UserRepository;
import com.puber.blog.service.DemoService;
import com.puber.blog.utils.SlugUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 演示服务实现类
 *
 * @author puber
 * @version 1.0.0
 * @since 2026-04-16
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DemoServiceImpl implements DemoService {

    private final DemoRepository demoRepository;
    private final UserRepository userRepository;

    /**
     * 获取已发布演示列表（分页）
     */
    @Override
    public Page<DemoVO> getPublishedDemos(Pageable pageable) {
        log.debug("获取已发布演示列表");
        Page<Demo> demos = demoRepository.findAllPublished("PUBLISHED", pageable);
        return demos.map(this::convertToVO);
    }

    /**
     * 根据状态获取演示列表（后台管理）
     */
    @Override
    public Page<DemoVO> getDemosByStatus(String status, Pageable pageable) {
        log.debug("按状态获取演示列表：{}", status);

        if ("ALL".equals(status)) {
            Page<Demo> demos = demoRepository.findAll(pageable);
            return demos.map(this::convertToVO);
        } else {
            Page<Demo> demos = demoRepository.findByStatus(status, pageable);
            return demos.map(this::convertToVO);
        }
    }

    /**
     * 根据slug获取演示详情（前台展示）
     */
    @Override
    public DemoVO getDemoBySlug(String slug) {
        log.info("根据slug获取演示详情：{}", slug);

        Demo demo = demoRepository.findBySlug(slug)
                .orElseThrow(() -> new BusinessException(404, "演示不存在：" + slug));

        // 增加浏览量
        incrementViewCount(demo.getId());

        return convertToVO(demo);
    }

    /**
     * 根据slug获取演示详情（iframe嵌入，不增加浏览量）
     */
    @Override
    public DemoVO getDemoBySlugWithoutViewCount(String slug) {
        log.info("根据slug获取演示详情（iframe嵌入，不计数）：{}", slug);

        Demo demo = demoRepository.findBySlug(slug)
                .orElseThrow(() -> new BusinessException(404, "演示不存在：" + slug));

        // 不增加浏览量（避免iframe重复计数）
        return convertToVO(demo);
    }

    /**
     * 根据ID获取演示详情（后台管理）
     */
    @Override
    public DemoVO getDemoById(Long id) {
        log.debug("根据ID获取演示详情：{}", id);

        Demo demo = demoRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "演示不存在：" + id));

        return convertToVO(demo);
    }

    /**
     * 创建演示
     */
    @Override
    @Transactional
    @LogRecord(operation = "创建演示", level = LogLevel.INFO, recordParams = true, recordTime = true)
    public Demo createDemo(DemoDTO dto, Long authorId) {
        log.info("创建演示：{}", dto.getName());

        // 检查作者是否存在
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new BusinessException(404, "作者不存在"));

        // 处理slug
        String slug = dto.getSlug();
        if (slug == null || slug.trim().isEmpty()) {
            slug = SlugUtils.toSlug(dto.getName());
        }

        // 检查slug是否已存在
        if (checkSlugExists(slug, null)) {
            slug = SlugUtils.toSlugWithTimestamp(dto.getName());
        }

        // 创建演示实体
        Demo demo = Demo.builder()
                .name(dto.getName())
                .slug(slug)
                .description(dto.getDescription())
                .fullHtmlContent(dto.getFullHtmlContent())
                .status(dto.getStatus() != null ? dto.getStatus() : "DRAFT")
                .viewCount(0L)
                .authorId(authorId)
                .publishedAt("PUBLISHED".equals(dto.getStatus()) ? LocalDateTime.now() : null)
                .build();

        // 保存演示
        return demoRepository.save(demo);
    }

    /**
     * 更新演示
     */
    @Override
    @Transactional
    @LogRecord(operation = "编辑演示", level = LogLevel.INFO, recordParams = true, recordTime = true)
    public Demo updateDemo(Long id, DemoDTO dto) {
        log.info("更新演示：{}", id);

        // 获取现有演示
        Demo demo = demoRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "演示不存在：" + id));

        // 处理slug
        String slug = dto.getSlug();
        if (slug == null || slug.trim().isEmpty()) {
            slug = SlugUtils.toSlug(dto.getName());
        }

        // 检查slug是否已存在（排除自己）
        if (checkSlugExists(slug, id)) {
            slug = SlugUtils.toSlugWithTimestamp(dto.getName());
        }

        // 更新演示属性
        demo.setName(dto.getName());
        demo.setSlug(slug);
        demo.setDescription(dto.getDescription());
        demo.setFullHtmlContent(dto.getFullHtmlContent());

        // 处理状态变更
        String oldStatus = demo.getStatus();
        String newStatus = dto.getStatus() != null ? dto.getStatus() : oldStatus;

        if (!"PUBLISHED".equals(oldStatus) && "PUBLISHED".equals(newStatus)) {
            // 从草稿变为发布，设置发布时间
            demo.setPublishedAt(LocalDateTime.now());
        }

        demo.setStatus(newStatus);

        return demoRepository.save(demo);
    }

    /**
     * 删除演示
     */
    @Override
    @Transactional
    @LogRecord(operation = "删除演示", level = LogLevel.WARN, recordParams = true, recordTime = true)
    public void deleteDemo(Long id) {
        log.warn("删除演示：id={}", id);

        Demo demo = demoRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "演示不存在：" + id));

        demoRepository.delete(demo);
    }

    /**
     * 增加浏览量
     */
    @Override
    @Transactional
    public void incrementViewCount(Long id) {
        log.debug("增加演示浏览量：{}", id);

        Demo demo = demoRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "演示不存在：" + id));

        demo.setViewCount(demo.getViewCount() + 1);
        demoRepository.save(demo);
    }

    /**
     * 检查slug是否存在
     */
    @Override
    public boolean checkSlugExists(String slug, Long excludeId) {
        if (slug == null || slug.trim().isEmpty()) {
            return false;
        }

        Demo existingDemo = demoRepository.findBySlug(slug).orElse(null);

        if (existingDemo == null) {
            return false;
        }

        return excludeId == null || !existingDemo.getId().equals(excludeId);
    }

    /**
     * 将Demo实体转换为DemoVO
     *
     * @param demo 演示实体
     * @return DemoVO 演示视图对象
     */
    private DemoVO convertToVO(Demo demo) {
        // 获取作者信息
        DemoVO.AuthorVO authorVO = null;
        if (demo.getAuthorId() != null) {
            User author = userRepository.findById(demo.getAuthorId()).orElse(null);
            if (author != null) {
                authorVO = DemoVO.AuthorVO.builder()
                        .id(author.getId())
                        .username(author.getUsername())
                        .nickname(author.getNickname())
                        .avatar(author.getAvatar())
                        .build();
            }
        }

        return DemoVO.builder()
                .id(demo.getId())
                .name(demo.getName())
                .slug(demo.getSlug())
                .description(demo.getDescription())
                .fullHtmlContent(demo.getFullHtmlContent())
                .status(demo.getStatus())
                .viewCount(demo.getViewCount())
                .author(authorVO)
                .publishedAt(demo.getPublishedAt())
                .createdAt(demo.getCreatedAt())
                .updatedAt(demo.getUpdatedAt())
                .build();
    }
}