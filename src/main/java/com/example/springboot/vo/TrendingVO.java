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
@Schema(description = "热门文章排行单项")
public class TrendingVO {

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

    @Schema(description = "热度分值，分值越大越热门")
    private Double hotScore;

    @Schema(description = "排行榜名次，从1开始")
    private Integer rank;

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

    @Schema(description = "发布时间 yyyy-MM-dd HH:mm:ss")
    private String createdAt;
}