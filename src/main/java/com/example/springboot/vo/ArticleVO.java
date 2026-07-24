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
@Schema(description = "文章基础信息（列表卡片展示）")
public class ArticleVO {

    @Schema(description = "文章ID")
    private String id;

    @Schema(description = "文章标题")
    private String title;

    @Schema(description = "文章URL别名，用于自定义链接")
    private String slug;

    @Schema(description = "文章摘要，列表页展示")
    private String summary;

    @Schema(description = "封面图URL")
    private String cover;

    @Schema(description = "浏览量")
    private Long views;

    @Schema(description = "点赞数")
    private Long likes;

    @Schema(description = "评论数")
    private Integer commentCount;

    @Schema(description = "所属分类")
    private CategoryVO category;

    @Schema(description = "标签列表")
    private List<TagVO> tags;

    @Schema(description = "创建时间，格式 yyyy-MM-dd HH:mm:ss")
    private String createdAt;
}