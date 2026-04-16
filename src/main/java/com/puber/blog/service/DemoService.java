package com.puber.blog.service;

import com.puber.blog.dto.DemoDTO;
import com.puber.blog.dto.DemoVO;
import com.puber.blog.entity.Demo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 演示服务接口
 *
 * @author puber
 * @version 1.0.0
 * @since 2026-04-16
 */
public interface DemoService {

    /**
     * 获取已发布演示列表（分页）
     *
     * @param pageable 分页参数
     * @return Page<DemoVO> 演示分页列表
     */
    Page<DemoVO> getPublishedDemos(Pageable pageable);

    /**
     * 根据状态获取演示列表（后台管理）
     *
     * @param status 状态（ALL/PUBLISHED/DRAFT）
     * @param pageable 分页参数
     * @return Page<DemoVO> 演示分页列表
     */
    Page<DemoVO> getDemosByStatus(String status, Pageable pageable);

    /**
     * 根据slug获取演示详情（前台展示）
     *
     * @param slug 演示别名
     * @return DemoVO 演示详情
     */
    DemoVO getDemoBySlug(String slug);

    /**
     * 根据slug获取演示详情（iframe嵌入，不增加浏览量）
     *
     * @param slug 演示别名
     * @return DemoVO 演示详情
     */
    DemoVO getDemoBySlugWithoutViewCount(String slug);

    /**
     * 根据ID获取演示详情（后台管理）
     *
     * @param id 演示ID
     * @return DemoVO 演示详情
     */
    DemoVO getDemoById(Long id);

    /**
     * 创建演示
     *
     * @param dto 演示DTO
     * @param authorId 作者ID
     * @return Demo 创建的演示实体
     */
    Demo createDemo(DemoDTO dto, Long authorId);

    /**
     * 更新演示
     *
     * @param id 演示ID
     * @param dto 演示DTO
     * @return Demo 更新后的演示实体
     */
    Demo updateDemo(Long id, DemoDTO dto);

    /**
     * 删除演示
     *
     * @param id 演示ID
     */
    void deleteDemo(Long id);

    /**
     * 增加浏览量
     *
     * @param id 演示ID
     */
    void incrementViewCount(Long id);

    /**
     * 检查slug是否存在
     *
     * @param slug 演示别名
     * @param excludeId 排除的演示ID（用于更新时检查）
     * @return boolean 是否存在
     */
    boolean checkSlugExists(String slug, Long excludeId);
}