package com.puber.blog.repository;

import com.puber.blog.entity.ArticleTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 文章标签关联 Repository 接口
 * 提供文章标签关联表的 CRUD 操作和自定义查询
 *
 * @author puber
 * @version 1.0.0
 * @since 2026-04-13
 */
@Repository
public interface ArticleTagRepository extends JpaRepository<ArticleTag, Long> {

    /**
     * 根据文章ID查询所有标签关联
     *
     * @param articleId 文章ID
     * @return List<ArticleTag> 标签关联列表
     */
    List<ArticleTag> findByArticleId(Long articleId);

    /**
     * 根据标签ID查询所有文章关联
     *
     * @param tagId 标签ID
     * @return List<ArticleTag> 文章关联列表
     */
    List<ArticleTag> findByTagId(Long tagId);

    /**
     * 根据文章ID删除所有标签关联
     *
     * @param articleId 文章ID
     * @return int 删除的记录数
     */
    @Modifying
    @Query("DELETE FROM ArticleTag at WHERE at.articleId = :articleId")
    int deleteByArticleId(@Param("articleId") Long articleId);

    /**
     * 检查文章和标签是否已关联
     *
     * @param articleId 文章ID
     * @param tagId 标签ID
     * @return true: 已关联, false: 未关联
     */
    boolean existsByArticleIdAndTagId(Long articleId, Long tagId);

    /**
     * 统计标签下的文章数量
     *
     * @param tagId 标签ID
     * @return Long 文章数量
     */
    Long countByTagId(Long tagId);

    /**
     * 根据文章ID查询所有标签ID
     *
     * @param articleId 文章ID
     * @return List<Long> 标签ID列表
     */
    @Query("SELECT at.tagId FROM ArticleTag at WHERE at.articleId = :articleId")
    List<Long> findTagIdsByArticleId(@Param("articleId") Long articleId);

    /**
     * 根据文章ID和标签ID删除关联
     *
     * @param articleId 文章ID
     * @param tagId 标签ID
     * @return int 删除的记录数
     */
    @Modifying
    @Query("DELETE FROM ArticleTag at WHERE at.articleId = :articleId AND at.tagId = :tagId")
    int deleteByArticleIdAndTagId(@Param("articleId") Long articleId, @Param("tagId") Long tagId);
}