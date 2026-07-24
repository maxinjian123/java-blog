package com.example.springboot.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "搜索结果单条记录")
public class SearchVO {

    @Schema(description = "文章ID")
    private String id;

    @Schema(description = "文章标题")
    private String title;

    @Schema(description = "文章URL别名")
    private String slug;

    @Schema(description = "文章摘要")
    private String summary;

    @Schema(description = "封面图URL")
    private String cover;

    @Schema(description = "SEO自定义标题")
    private String seoTitle;

    @Schema(description = "SEO描述")
    private String seoDescription;

    @Schema(description = "SEO关键词")
    private String seoKeywords;

    @Schema(description = "浏览量")
    private Long views;

    @Schema(description = "评论数")
    private Integer commentCount;

    @Schema(description = "点赞数")
    private Long likes;

    @Schema(description = "原始正文，仅搜索结果展示时截断")
    private String content;

    @Schema(description = "搜索命中高亮片段（包含 <em>标签</em>）")
    private String highlight;

    @Schema(description = "所属分类")
    private CategoryVO category;

    @Schema(description = "标签列表")
    private List<TagVO> tags;

    @Schema(description = "发布时间 yyyy-MM-dd HH:mm:ss")
    private String createdAt;
}