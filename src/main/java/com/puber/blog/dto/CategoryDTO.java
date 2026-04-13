package com.puber.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分类数据传输对象（DTO）
 * 用于接收前端提交的分类数据（创建和编辑）
 *
 * @author puber
 * @version 1.0.0
 * @since 2026-04-13
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDTO {

    /**
     * 分类名称
     */
    private String name;

    /**
     * 分类别名（URL友好）
     * 如果不提供，系统会自动根据name生成
     */
    private String slug;

    /**
     * 分类描述
     */
    private String description;

    /**
     * 排序顺序
     * 数值越小，排名越靠前
     */
    private Integer sortOrder;
}