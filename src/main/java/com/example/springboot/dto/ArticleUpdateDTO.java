package com.example.springboot.dto;

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
@Schema(description = "编辑文章请求（增量更新，字段传null表示不修改）")
public class ArticleUpdateDTO {

    @Schema(description = "文章标题")
    private String title;

    @Schema(description = "自定义URL别名")
    private String slug;

    @Schema(description = "文章摘要")
    private String summary;

    @Schema(description = "Markdown正文内容")
    private String content;

    @Schema(description = "封面图URL")
    private String cover;

    @Schema(description = "所属分类ID")
    private String categoryId;

    @Schema(description = "标签ID列表")
    private List<String> tagIds;

    @Schema(description = "状态: 0-草稿, 1-已发布, 2-隐藏")
    private Integer status;

    @Schema(description = "SEO标题")
    private String seoTitle;

    @Schema(description = "SEO关键词")
    private String seoKeywords;

    @Schema(description = "SEO描述")
    private String seoDescription;
}