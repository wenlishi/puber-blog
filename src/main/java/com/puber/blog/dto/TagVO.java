package com.puber.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 标签视图对象（VO）
 * 用于返回给前端展示的标签数据
 *
 * @author puber
 * @version 1.0.0
 * @since 2026-04-13
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TagVO {

    /**
     * 标签ID
     */
    private Long id;

    /**
     * 标签名称
     */
    private String name;

    /**
     * 标签别名（URL友好）
     */
    private String slug;

    /**
     * 该标签下的文章数量
     */
    private Long articleCount;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}