package com.example.springboot.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.keygen.KeyGenerators;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文章主表 实体类。
 *
 * @author <a href="https://github.com/maxinjian123">潇潇</a>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("articles")
public class Articles implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 文章ID (字符串)
     */
    @Id(keyType = KeyType.Generator, value = KeyGenerators.snowFlakeId)
    private String id;

    /**
     * 作者ID (字符串)
     */
    private String authorId;

    /**
     * 所属分类ID (字符串)
     */
    private String categoryId;

    /**
     * 文章标题
     */
    private String title;

    /**
     * 自定义URL别名
     */
    private String slug;

    /**
     * 文章摘要/简介
     */
    private String summary;

    /**
     * Markdown正文内容
     */
    private String content;

    /**
     * 封面图URL
     */
    private String cover;

    /**
     * 独立 SEO 标题 (若为空则用文章 title)
     */
    private String seoTitle;

    /**
     * 独立 SEO 关键词
     */
    private String seoKeywords;

    /**
     * 独立 SEO 描述 (若为空则用 summary)
     */
    private String seoDescription;

    /**
     * 状态: 0-草稿, 1-已发布, 2-隐藏
     */
    private Integer status;

    /**
     * 浏览量 (Redis 异步同步)
     */
    private Long views;

    /**
     * 点赞数
     */
    private Long likes;

    /**
     * 发布时间
     */
    private LocalDateTime createdAt;

    /**
     * 修改时间
     */
    private LocalDateTime updatedAt;

    /**
     * 逻辑删除标识
     */
    @Column(isLogicDelete = true)
    private Integer deleted;

}