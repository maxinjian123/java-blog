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
@Schema(description = "后台仪表盘首页概览数据")
public class DashboardVO {

    @Schema(description = "文章总数（含草稿）")
    private long articleCount;

    @Schema(description = "评论总数")
    private long commentCount;

    @Schema(description = "历史累计浏览量")
    private long totalViews;

    @Schema(description = "历史累计点赞数")
    private long totalLikes;

    @Schema(description = "最新发布的评论列表，用于快速审核")
    private List<CommentVO> latestComments;
}