package com.puber.blog.repository;

import com.puber.blog.entity.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 文章 Repository 接口
 * 提供文章数据的 CRUD 操作和复杂查询
 *
 * @author puber
 * @version 1.0.0
 * @since 2026-04-13
 */
@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {

    /**
     * 根据别名查询文章
     *
     * @param slug 文章别名
     * @return Optional<Article> 文章对象
     */
    Optional<Article> findBySlug(String slug);

    /**
     * 根据状态查询文章列表（分页）
     *
     * @param status 文章状态
     * @param pageable 分页参数
     * @return Page<Article> 文章分页列表
     */
    Page<Article> findByStatus(String status, Pageable pageable);

    /**
     * 根据分类 ID 查询文章列表（分页）
     *
     * @param categoryId 分类 ID
     * @param status 文章状态
     * @param pageable 分页参数
     * @return Page<Article> 文章分页列表
     */
    Page<Article> findByCategoryIdAndStatus(Long categoryId, String status, Pageable pageable);

    /**
     * 根据作者 ID 查询文章列表（分页）
     *
     * @param authorId 作者 ID
     * @param pageable 分页参数
     * @return Page<Article> 文章分页列表
     */
    Page<Article> findByAuthorId(Long authorId, Pageable pageable);

    /**
     * 查询置顶文章列表
     *
     * @param isTop 是否置顶
     * @param status 文章状态
     * @return List<Article> 置顶文章列表
     */
    List<Article> findByIsTopAndStatus(Boolean isTop, String status);

    /**
     * 查询所有已发布文章（按发布时间倒序）
     *
     * @param status 文章状态
     * @param pageable 分页参数
     * @return Page<Article> 文章分页列表
     */
    @Query("SELECT a FROM Article a WHERE a.status = :status ORDER BY a.isTop DESC, a.publishedAt DESC")
    Page<Article> findAllPublished(@Param("status") String status, Pageable pageable);

    /**
     * 搜索文章标题或内容包含关键字的文章
     *
     * @param keyword 搜索关键字
     * @param status 文章状态
     * @param pageable 分页参数
     * @return Page<Article> 文章分页列表
     */
    @Query("SELECT a FROM Article a WHERE a.status = :status AND (a.title LIKE %:keyword% OR a.content LIKE %:keyword%)")
    Page<Article> searchByKeyword(@Param("keyword") String keyword, @Param("status") String status, Pageable pageable);

    /**
     * 统计文章数量
     *
     * @param status 文章状态
     * @return Long 文章数量
     */
    Long countByStatus(String status);

    /**
     * 检查别名是否存在
     *
     * @param slug 文章别名
     * @return true: 存在, false: 不存在
     */
    boolean existsBySlug(String slug);

    /**
     * 统计指定分类下的文章数量
     *
     * @param categoryId 分类ID
     * @param status 文章状态
     * @return Long 文章数量
     */
    Long countByCategoryIdAndStatus(Long categoryId, String status);

    /**
     * 查询指定状态的文章列表（按发布时间倒序）
     *
     * @param status 文章状态
     * @return List<Article> 文章列表
     */
    List<Article> findByStatusOrderByPublishedAtDesc(String status);

    /**
     * 查询最新文章列表（按创建时间倒序）
     *
     * @param pageable 分页参数
     * @return List<Article> 文章列表
     */
    List<Article> findAllByOrderByCreatedAtDesc(Pageable pageable);

    /**
     * 查询热门文章列表（按浏览量倒序）
     *
     * @param pageable 分页参数
     * @return List<Article> 文章列表
     */
    List<Article> findAllByOrderByViewCountDesc(Pageable pageable);

    /**
     * 统计所有文章的总浏览量
     *
     * @return Long 总浏览量
     */
    @Query("SELECT SUM(a.viewCount) FROM Article a")
    Long sumViewCount();
}