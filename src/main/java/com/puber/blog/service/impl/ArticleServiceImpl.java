package com.puber.blog.service.impl;

import com.puber.blog.dto.*;
import com.puber.blog.entity.Article;
import com.puber.blog.entity.ArticleTag;
import com.puber.blog.entity.Category;
import com.puber.blog.entity.Tag;
import com.puber.blog.entity.User;
import com.puber.blog.exception.BusinessException;
import com.puber.blog.repository.*;
import com.puber.blog.service.ArticleService;
import com.puber.blog.utils.MarkdownUtils;
import com.puber.blog.utils.SlugUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 文章业务服务实现类
 * 实现文章的业务逻辑处理
 *
 * @author puber
 * @version 1.0.0
 * @since 2026-04-13
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {

    private final ArticleRepository articleRepository;
    private final ArticleTagRepository articleTagRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final UserRepository userRepository;

    /**
     * 获取已发布文章列表（分页）
     *
     * @param pageable 分页参数
     * @return Page<ArticleListVO> 文章分页列表
     */
    @Override
    public Page<ArticleListVO> getPublishedArticles(Pageable pageable) {
        log.debug("获取已发布文章列表");

        Page<Article> articles = articleRepository.findAllPublished("PUBLISHED", pageable);
        return articles.map(this::convertToListVO);
    }

    /**
     * 按分类筛选文章（分页）
     *
     * @param categoryId 分类ID
     * @param pageable 分页参数
     * @return Page<ArticleListVO> 文章分页列表
     */
    @Override
    public Page<ArticleListVO> getArticlesByCategory(Long categoryId, Pageable pageable) {
        log.debug("按分类筛选文章：{}", categoryId);

        Page<Article> articles = articleRepository.findByCategoryIdAndStatus(categoryId, "PUBLISHED", pageable);
        return articles.map(this::convertToListVO);
    }

    /**
     * 按标签筛选文章（分页）
     *
     * @param tagId 标签ID
     * @param pageable 分页参数
     * @return Page<ArticleListVO> 文章分页列表
     */
    @Override
    public Page<ArticleListVO> getArticlesByTag(Long tagId, Pageable pageable) {
        log.debug("按标签筛选文章：{}", tagId);

        // 查询该标签下的所有文章ID
        List<ArticleTag> articleTags = articleTagRepository.findByTagId(tagId);
        List<Long> articleIds = articleTags.stream()
                .map(ArticleTag::getArticleId)
                .collect(Collectors.toList());

        if (articleIds.isEmpty()) {
            return Page.empty(pageable);
        }

        // 查询文章列表（只查询已发布的）
        List<Article> articles = articleRepository.findAllById(articleIds).stream()
                .filter(a -> "PUBLISHED".equals(a.getStatus()))
                .collect(Collectors.toList());

        // 手动分页（简化处理，实际应用中应该使用更高效的方式）
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), articles.size());
        List<ArticleListVO> content = articles.subList(start, end).stream()
                .map(this::convertToListVO)
                .collect(Collectors.toList());

        return new org.springframework.data.domain.PageImpl<>(content, pageable, articles.size());
    }

    /**
     * 搜索文章（分页）
     *
     * @param keyword 关键字
     * @param pageable 分页参数
     * @return Page<ArticleListVO> 文章分页列表
     */
    @Override
    public Page<ArticleListVO> searchArticles(String keyword, Pageable pageable) {
        log.debug("搜索文章：{}", keyword);

        Page<Article> articles = articleRepository.searchByKeyword(keyword, "PUBLISHED", pageable);
        return articles.map(this::convertToListVO);
    }

    /**
     * 根据slug获取文章详情（前台）
     *
     * @param slug 文章别名
     * @return ArticleVO 文章详情
     */
    @Override
    public ArticleVO getArticleBySlug(String slug) {
        log.debug("根据slug获取文章详情：{}", slug);

        Article article = articleRepository.findBySlug(slug)
                .orElseThrow(() -> new BusinessException(404, "文章不存在：" + slug));

        // 增加浏览量
        incrementViewCount(article.getId());

        return convertToVO(article);
    }

    /**
     * 根据ID获取文章详情（后台）
     *
     * @param id 文章ID
     * @return ArticleVO 文章详情
     */
    @Override
    public ArticleVO getArticleById(Long id) {
        log.debug("根据ID获取文章详情：{}", id);

        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "文章不存在：" + id));

        return convertToVO(article);
    }

    /**
     * 创建文章
     *
     * @param dto 文章DTO
     * @param authorId 作者ID
     * @return Article 创建的文章实体
     */
    @Override
    @Transactional
    public Article createArticle(ArticleDTO dto, Long authorId) {
        log.info("创建文章：{}", dto.getTitle());

        // 检查作者是否存在
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new BusinessException(404, "作者不存在"));

        // 处理slug
        String slug = dto.getSlug();
        if (slug == null || slug.trim().isEmpty()) {
            slug = SlugUtils.toSlug(dto.getTitle());
        }

        // 检查slug是否已存在
        if (checkSlugExists(slug, null)) {
            slug = SlugUtils.toSlugWithTimestamp(dto.getTitle());
        }

        // 处理摘要
        String summary = dto.getSummary();
        if (summary == null || summary.trim().isEmpty()) {
            summary = MarkdownUtils.extractSummary(dto.getContent(), 200);
        }

        // 处理分类
        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new BusinessException(404, "分类不存在"));
        }

        // 创建文章实体
        Article article = Article.builder()
                .title(dto.getTitle())
                .slug(slug)
                .summary(summary)
                .content(dto.getContent())
                .coverImage(dto.getCoverImage())
                .status(dto.getStatus() != null ? dto.getStatus() : "DRAFT")
                .isTop(dto.getIsTop() != null ? dto.getIsTop() : false)
                .viewCount(0L)
                .isCommentEnabled(dto.getIsCommentEnabled() != null ? dto.getIsCommentEnabled() : true)
                .authorId(authorId)
                .categoryId(dto.getCategoryId())
                .publishedAt("PUBLISHED".equals(dto.getStatus()) ? LocalDateTime.now() : null)
                .build();

        // 保存文章
        article = articleRepository.save(article);

        // 处理标签关联
        if (dto.getTagIds() != null && !dto.getTagIds().isEmpty()) {
            for (Long tagId : dto.getTagIds()) {
                Tag tag = tagRepository.findById(tagId)
                        .orElseThrow(() -> new BusinessException(404, "标签不存在：" + tagId));

                ArticleTag articleTag = ArticleTag.builder()
                        .articleId(article.getId())
                        .tagId(tagId)
                        .build();

                articleTagRepository.save(articleTag);
            }
        }

        return article;
    }

    /**
     * 更新文章
     *
     * @param id 文章ID
     * @param dto 文章DTO
     * @return Article 更新后的文章实体
     */
    @Override
    @Transactional
    public Article updateArticle(Long id, ArticleDTO dto) {
        log.info("更新文章：{}", id);

        // 获取现有文章
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "文章不存在：" + id));

        // 处理slug
        String slug = dto.getSlug();
        if (slug == null || slug.trim().isEmpty()) {
            slug = SlugUtils.toSlug(dto.getTitle());
        }

        // 检查slug是否已存在（排除自己）
        if (checkSlugExists(slug, id)) {
            slug = SlugUtils.toSlugWithTimestamp(dto.getTitle());
        }

        // 处理摘要
        String summary = dto.getSummary();
        if (summary == null || summary.trim().isEmpty()) {
            summary = MarkdownUtils.extractSummary(dto.getContent(), 200);
        }

        // 处理分类
        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new BusinessException(404, "分类不存在"));
        }

        // 更新文章属性
        article.setTitle(dto.getTitle());
        article.setSlug(slug);
        article.setSummary(summary);
        article.setContent(dto.getContent());
        article.setCoverImage(dto.getCoverImage());
        article.setCategoryId(dto.getCategoryId());

        // 处理状态变更
        String oldStatus = article.getStatus();
        String newStatus = dto.getStatus() != null ? dto.getStatus() : oldStatus;

        if (!"PUBLISHED".equals(oldStatus) && "PUBLISHED".equals(newStatus)) {
            // 从草稿变为发布，设置发布时间
            article.setPublishedAt(LocalDateTime.now());
        }

        article.setStatus(newStatus);
        article.setIsTop(dto.getIsTop() != null ? dto.getIsTop() : article.getIsTop());
        article.setIsCommentEnabled(dto.getIsCommentEnabled() != null ? dto.getIsCommentEnabled() : article.getIsCommentEnabled());

        // 保存文章
        article = articleRepository.save(article);

        // 更新标签关联：先删除旧的关联，再添加新的关联
        articleTagRepository.deleteByArticleId(article.getId());

        if (dto.getTagIds() != null && !dto.getTagIds().isEmpty()) {
            for (Long tagId : dto.getTagIds()) {
                Tag tag = tagRepository.findById(tagId)
                        .orElseThrow(() -> new BusinessException(404, "标签不存在：" + tagId));

                ArticleTag articleTag = ArticleTag.builder()
                        .articleId(article.getId())
                        .tagId(tagId)
                        .build();

                articleTagRepository.save(articleTag);
            }
        }

        return article;
    }

    /**
     * 删除文章
     *
     * @param id 文章ID
     */
    @Override
    @Transactional
    public void deleteArticle(Long id) {
        log.info("删除文章：{}", id);

        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "文章不存在：" + id));

        // 删除文章标签关联
        articleTagRepository.deleteByArticleId(id);

        // 删除文章
        articleRepository.delete(article);
    }

    /**
     * 置顶/取消置顶文章
     *
     * @param id 文章ID
     */
    @Override
    @Transactional
    public void toggleTop(Long id) {
        log.info("切换文章置顶状态：{}", id);

        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "文章不存在：" + id));

        article.setIsTop(!article.getIsTop());
        articleRepository.save(article);
    }

    /**
     * 增加浏览量
     *
     * @param id 文章ID
     */
    @Override
    @Transactional
    public void incrementViewCount(Long id) {
        log.debug("增加文章浏览量：{}", id);

        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "文章不存在：" + id));

        article.setViewCount(article.getViewCount() + 1);
        articleRepository.save(article);
    }

    /**
     * 按状态获取文章列表（后台管理）
     *
     * @param status 文章状态
     * @param pageable 分页参数
     * @return Page<ArticleListVO> 文章分页列表
     */
    @Override
    public Page<ArticleListVO> getArticlesByStatus(String status, Pageable pageable) {
        log.debug("按状态获取文章列表：{}", status);

        if ("ALL".equals(status)) {
            Page<Article> articles = articleRepository.findAll(pageable);
            return articles.map(this::convertToListVO);
        } else {
            Page<Article> articles = articleRepository.findByStatus(status, pageable);
            return articles.map(this::convertToListVO);
        }
    }

    /**
     * 获取归档文章（按年份月份分组）
     *
     * @return Map<Integer, Map<Integer, List<ArticleListVO>>> 归档数据
     */
    @Override
    public Map<Integer, Map<Integer, List<ArticleListVO>>> getArchiveArticles() {
        log.debug("获取归档文章");

        List<Article> articles = articleRepository.findByStatusOrderByPublishedAtDesc("PUBLISHED");

        Map<Integer, Map<Integer, List<ArticleListVO>>> archive = new TreeMap<>(Collections.reverseOrder());

        for (Article article : articles) {
            if (article.getPublishedAt() == null) {
                continue;
            }

            int year = article.getPublishedAt().getYear();
            int month = article.getPublishedAt().getMonthValue();

            archive.computeIfAbsent(year, k -> new TreeMap<>(Collections.reverseOrder()))
                    .computeIfAbsent(month, k -> new ArrayList<>())
                    .add(convertToListVO(article));
        }

        return archive;
    }

    /**
     * 检查slug是否已存在
     *
     * @param slug 文章别名
     * @param excludeId 排除的文章ID
     * @return true: 已存在, false: 不存在
     */
    @Override
    public boolean checkSlugExists(String slug, Long excludeId) {
        if (slug == null || slug.trim().isEmpty()) {
            return false;
        }

        Article existingArticle = articleRepository.findBySlug(slug).orElse(null);

        if (existingArticle == null) {
            return false;
        }

        return excludeId == null || !existingArticle.getId().equals(excludeId);
    }

    /**
     * 将Article实体转换为ArticleListVO
     *
     * @param article 文章实体
     * @return ArticleListVO 文章列表视图对象
     */
    private ArticleListVO convertToListVO(Article article) {
        // 获取分类信息
        String categoryName = null;
        String categorySlug = null;
        if (article.getCategoryId() != null) {
            Category category = categoryRepository.findById(article.getCategoryId()).orElse(null);
            if (category != null) {
                categoryName = category.getName();
                categorySlug = category.getSlug();
            }
        }

        // 获取标签名称列表
        List<Long> tagIds = articleTagRepository.findTagIdsByArticleId(article.getId());
        String tagNames = null;
        if (!tagIds.isEmpty()) {
            List<Tag> tags = tagRepository.findAllById(tagIds);
            tagNames = tags.stream()
                    .map(Tag::getName)
                    .collect(Collectors.joining(", "));
        }

        // 获取作者名称
        String authorName = null;
        if (article.getAuthorId() != null) {
            User author = userRepository.findById(article.getAuthorId()).orElse(null);
            if (author != null) {
                authorName = author.getNickname() != null ? author.getNickname() : author.getUsername();
            }
        }

        return ArticleListVO.builder()
                .id(article.getId())
                .title(article.getTitle())
                .slug(article.getSlug())
                .summary(article.getSummary())
                .coverImage(article.getCoverImage())
                .status(article.getStatus())
                .isTop(article.getIsTop())
                .viewCount(article.getViewCount())
                .categoryName(categoryName)
                .categorySlug(categorySlug)
                .tagNames(tagNames)
                .authorName(authorName)
                .publishedAt(article.getPublishedAt())
                .createdAt(article.getCreatedAt())
                .build();
    }

    /**
     * 将Article实体转换为ArticleVO
     *
     * @param article 文章实体
     * @return ArticleVO 文章视图对象
     */
    private ArticleVO convertToVO(Article article) {
        // 获取分类信息
        CategoryVO categoryVO = null;
        if (article.getCategoryId() != null) {
            Category category = categoryRepository.findById(article.getCategoryId()).orElse(null);
            if (category != null) {
                categoryVO = CategoryVO.builder()
                        .id(category.getId())
                        .name(category.getName())
                        .slug(category.getSlug())
                        .description(category.getDescription())
                        .sortOrder(category.getSortOrder())
                        .createdAt(category.getCreatedAt())
                        .build();
            }
        }

        // 获取标签列表
        List<TagVO> tagVOs = new ArrayList<>();
        List<Long> tagIds = articleTagRepository.findTagIdsByArticleId(article.getId());
        if (!tagIds.isEmpty()) {
            List<Tag> tags = tagRepository.findAllById(tagIds);
            tagVOs = tags.stream()
                    .map(tag -> TagVO.builder()
                            .id(tag.getId())
                            .name(tag.getName())
                            .slug(tag.getSlug())
                            .createdAt(tag.getCreatedAt())
                            .build())
                    .collect(Collectors.toList());
        }

        // 获取标签名称列表（逗号分隔，用于SEO）
        String tagNames = null;
        if (!tagVOs.isEmpty()) {
            tagNames = tagVOs.stream()
                    .map(TagVO::getName)
                    .collect(Collectors.joining(", "));
        }

        // 获取作者信息
        ArticleVO.AuthorVO authorVO = null;
        if (article.getAuthorId() != null) {
            User author = userRepository.findById(article.getAuthorId()).orElse(null);
            if (author != null) {
                authorVO = ArticleVO.AuthorVO.builder()
                        .id(author.getId())
                        .username(author.getUsername())
                        .nickname(author.getNickname())
                        .avatar(author.getAvatar())
                        .build();
            }
        }

        // 将Markdown转换为HTML
        String contentHtml = MarkdownUtils.toHtml(article.getContent());

        return ArticleVO.builder()
                .id(article.getId())
                .title(article.getTitle())
                .slug(article.getSlug())
                .summary(article.getSummary())
                .contentHtml(contentHtml)
                .contentMarkdown(article.getContent())
                .coverImage(article.getCoverImage())
                .status(article.getStatus())
                .isTop(article.getIsTop())
                .viewCount(article.getViewCount())
                .isCommentEnabled(article.getIsCommentEnabled())
                .category(categoryVO)
                .tags(tagVOs)
                .tagNames(tagNames)
                .author(authorVO)
                .publishedAt(article.getPublishedAt())
                .createdAt(article.getCreatedAt())
                .updatedAt(article.getUpdatedAt())
                .build();
    }
}