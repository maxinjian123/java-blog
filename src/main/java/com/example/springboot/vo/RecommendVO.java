package com.example.springboot.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "个性化推荐文章条目")
public class RecommendVO {

    @Schema(description = "推荐文章ID")
    private String id;

    @Schema(description = "文章标题")
    private String title;

    @Schema(description = "文章URL别名")
    private String slug;

    @Schema(description = "文章摘要")
    private String summary;

    @Schema(description = "封面图URL")
    private String cover;

    @Schema(description = "浏览量")
    private Long views;

    @Schema(description = "点赞数")
    private Long likes;

    @Schema(description = "相似度得分，范围0~1，越大越相关")
    private Double similarity;

    @Schema(description = "推荐理由，用于前端副标题展示，例如：同分类「Java」，包含相似标签: SpringBoot, Redis")
    private String recommendReason;

    @Schema(description = "发布时间 yyyy-MM-dd HH:mm:ss")
    private String createdAt;
}