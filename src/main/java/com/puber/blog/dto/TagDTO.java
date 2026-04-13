package com.puber.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 标签数据传输对象（DTO）
 * 用于接收前端提交的标签数据（创建和编辑）
 *
 * @author puber
 * @version 1.0.0
 * @since 2026-04-13
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TagDTO {

    /**
     * 标签名称
     */
    private String name;

    /**
     * 标签别名（URL友好）
     * 如果不提供，系统会自动根据name生成
     */
    private String slug;
}