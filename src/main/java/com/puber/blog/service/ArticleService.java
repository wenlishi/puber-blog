package com.puber.blog.service;

import com.puber.blog.dto.ArticleDTO;
import com.puber.blog.dto.ArticleListVO;
import com.puber.blog.dto.ArticleVO;
import com.puber.blog.entity.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

/**
 * 文章业务服务接口
 * 提供文章的业务逻辑处理
 *
 * @author puber
 * @version 1.0.0
 * @since 2026-04-13
 */
public interface ArticleService {

    /**
     * 获取已发布文章列表（分页）
     * 用于前台展示，包含置顶文章
     *
     * @param pageable 分页参数
     * @return Page<ArticleListVO> 文章分页列表
     */
    Page<ArticleListVO> getPublishedArticles(Pageable pageable);

    /**
     * 按分类筛选文章（分页）
     *
     * @param categoryId 分类ID
     * @param pageable 分页参数
     * @return Page<ArticleListVO> 文章分页列表
     */
    Page<ArticleListVO> getArticlesByCategory(Long categoryId, Pageable pageable);

    /**
     * 按标签筛选文章（分页）
     *
     * @param tagId 标签ID
     * @param pageable 分页参数
     * @return Page<ArticleListVO> 文章分页列表
     */
    Page<ArticleListVO> getArticlesByTag(Long tagId, Pageable pageable);

    /**
     * 搜索文章（分页）
     *
     * @param keyword 关键字
     * @param pageable 分页参数
     * @return Page<ArticleListVO> 文章分页列表
     */
    Page<ArticleListVO> searchArticles(String keyword, Pageable pageable);

    /**
     * 根据slug获取文章详情（前台）
     * 包含Markdown转HTML、浏览量+1
     *
     * @param slug 文章别名
     * @return ArticleVO 文章详情
     */
    ArticleVO getArticleBySlug(String slug);

    /**
     * 根据ID获取文章详情（后台）
     * 用于编辑时加载文章数据
     *
     * @param id 文章ID
     * @return ArticleVO 文章详情
     */
    ArticleVO getArticleById(Long id);

    /**
     * 创建文章
     * 处理Markdown转HTML、标签关联、slug生成等
     *
     * @param dto 文章DTO
     * @param authorId 作者ID
     * @return Article 创建的文章实体
     */
    Article createArticle(ArticleDTO dto, Long authorId);

    /**
     * 更新文章
     *
     * @param id 文章ID
     * @param dto 文章DTO
     * @return Article 更新后的文章实体
     */
    Article updateArticle(Long id, ArticleDTO dto);

    /**
     * 删除文章
     *
     * @param id 文章ID
     */
    void deleteArticle(Long id);

    /**
     * 置顶/取消置顶文章
     *
     * @param id 文章ID
     */
    void toggleTop(Long id);

    /**
     * 增加浏览量
     *
     * @param id 文章ID
     */
    void incrementViewCount(Long id);

    /**
     * 按状态获取文章列表（后台管理）
     *
     * @param status 文章状态（PUBLISHED/DRAFT/ALL）
     * @param pageable 分页参数
     * @return Page<ArticleListVO> 文章分页列表
     */
    Page<ArticleListVO> getArticlesByStatus(String status, Pageable pageable);

    /**
     * 获取归档文章（按年份月份分组）
     *
     * @return Map<Integer, Map<Integer, List<ArticleListVO>>> 归档数据
     */
    Map<Integer, Map<Integer, java.util.List<ArticleListVO>>> getArchiveArticles();

    /**
     * 检查slug是否已存在
     *
     * @param slug 文章别名
     * @param excludeId 排除的文章ID（用于更新时检查）
     * @return true: 已存在, false: 不存在
     */
    boolean checkSlugExists(String slug, Long excludeId);
}