package com.example.springboot.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleCreateDTO {

    @NotBlank(message = "文章标题不能为空")
    @Schema(description = "文章标题", required = true)
    private String title;

    @Schema(description = "自定义URL别名")
    private String slug;

    @Schema(description = "文章摘要")
    private String summary;

    @NotBlank(message = "文章内容不能为空")
    @Schema(description = "Markdown正文内容", required = true)
    private String content;

    @Schema(description = "封面图URL")
    private String cover;

    @Schema(description = "所属分类ID")
    private String categoryId;

    @Schema(description = "标签ID列表")
    private List<String> tagIds;

    @Schema(description = "状态: 0-草稿, 1-已发布")
    @Builder.Default
    private Integer status = 1;

    @Schema(description = "SEO标题")
    private String seoTitle;

    @Schema(description = "SEO关键词")
    private String seoKeywords;

    @Schema(description = "SEO描述")
    private String seoDescription;
}