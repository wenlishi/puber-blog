package com.puber.blog.dto;

import lombok.*;

import java.time.LocalDateTime;

/**
 * 演示视图对象（VO）
 * 用于返回给前端展示的演示数据
 *
 * @author puber
 * @version 1.0.0
 * @since 2026-04-16
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DemoVO {

    /**
     * 演示ID
     */
    private Long id;

    /**
     * 演示名称
     */
    private String name;

    /**
     * 演示别名
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
     * 状态
     */
    private String status;

    /**
     * 浏览次数
     */
    private Long viewCount;

    /**
     * 作者信息
     */
    private AuthorVO author;

    /**
     * 发布时间
     */
    private LocalDateTime publishedAt;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 作者信息内部类
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AuthorVO {
        private Long id;
        private String username;
        private String nickname;
        private String avatar;
    }
}