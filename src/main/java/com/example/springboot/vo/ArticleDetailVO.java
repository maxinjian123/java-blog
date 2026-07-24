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
@Schema(description = "文章完整详情（详情页展示）")
public class ArticleDetailVO {

    @Schema(description = "文章ID")
    private String id;

    @Schema(description = "文章标题")
    private String title;

    @Schema(description = "文章URL别名")
    private String slug;

    @Schema(description = "文章摘要")
    private String summary;

    @Schema(description = "Markdown 原始正文")
    private String content;

    @Schema(description = "渲染后的HTML正文")
    private String htmlContent;

    @Schema(description = "封面图URL")
    private String cover;

    @Schema(description = "浏览量")
    private Long views;

    @Schema(description = "点赞数")
    private Long likes;

    @Schema(description = "评论数")
    private Integer commentCount;

    @Schema(description = "状态: 0-草稿, 1-已发布")
    private Integer status;

    @Schema(description = "所属分类")
    private CategoryVO category;

    @Schema(description = "标签列表")
    private List<TagVO> tags;

    @Schema(description = "作者信息")
    private AuthorVO author;

    @Schema(description = "SEO自定义标题")
    private String seoTitle;

    @Schema(description = "SEO关键词，英文逗号分隔")
    private String seoKeywords;

    @Schema(description = "SEO描述")
    private String seoDescription;

    @Schema(description = "发布时间 yyyy-MM-dd HH:mm:ss")
    private String createdAt;

    @Schema(description = "最后更新时间 yyyy-MM-dd HH:mm:ss")
    private String updatedAt;
}