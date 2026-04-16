package com.puber.blog.dto;

import lombok.*;

/**
 * 演示数据传输对象（DTO）
 * 用于接收前端提交的演示数据
 *
 * @author puber
 * @version 1.0.0
 * @since 2026-04-16
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DemoDTO {

    /**
     * 演示ID（用于更新时标识）
     */
    private Long id;

    /**
     * 演示名称
     */
    private String name;

    /**
     * 演示别名（可选，自动生成）
     */
    private String slug;

    /**
     * 演示描述
     */
    private String description;

    /**
     * 完整HTML内容
     */
    private String fullHtmlContent;

    /**
     * 状态（PUBLISHED/DRAFT）
     */
    private String status;
}