 package com.example.springboot.controller;

import com.example.springboot.common.BaseResponse;
import com.example.springboot.common.ResultUtils;
import com.example.springboot.common.annotation.Auth;
import com.example.springboot.common.constant.RoleConstant;
import com.example.springboot.service.RecommendService;
import com.example.springboot.service.TrendingService;
import com.example.springboot.vo.RecommendVO;
import com.example.springboot.vo.TrendingVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "热门排行与推荐", description = "Redis驱动排行榜 + 内容相似推荐")
@RestController
@RequestMapping("/articles")
public class TrendingController {

    private final TrendingService trendingService;
    private final RecommendService recommendService;

    public TrendingController(TrendingService trendingService, RecommendService recommendService) {
        this.trendingService = trendingService;
        this.recommendService = recommendService;
    }

    @Operation(summary = "获取热门文章排行", description = "基于Redis ZSET的高性能排行榜，period: daily/weekly/monthly/all，默认daily")
    @GetMapping("/trending")
    public BaseResponse<List<TrendingVO>> getTrending(
            @Parameter(description = "榜单周期: daily(日榜), weekly(周榜), monthly(月榜), all(总榜)")
            @RequestParam(defaultValue = "daily") String period,
            @Parameter(description = "获取前N名，默认10，最大50")
            @RequestParam(defaultValue = "10") int limit) {
        List<TrendingVO> trending = trendingService.getTrending(period, limit);
        return ResultUtils.success(trending);
    }

    @Operation(summary = "获取个性化相关文章推荐", description = "基于标签/分类/关键词的内容相似度推荐，用于文章详情页")
    @GetMapping("/{id}/recommend")
    public BaseResponse<List<RecommendVO>> getRecommend(
            @Parameter(description = "文章ID") @PathVariable String id,
            @Parameter(description = "返回条数，默认4") @RequestParam(defaultValue = "4") int limit) {
        List<RecommendVO> recommend = recommendService.getRecommend(id, limit);
        return ResultUtils.success(recommend);
    }

    @Auth(role = RoleConstant.ADMIN)
    @Operation(summary = "重建排行榜数据", description = "根据浏览/点赞/评论和时间衰减重算热度分值，刷新Redis ZSET")
    @PostMapping("/trending/rebuild")
    public BaseResponse<Boolean> rebuildTrending() {
        trendingService.rebuildTrending();
        return ResultUtils.success(true, "排行榜已重建");
    }

    @Auth(role = RoleConstant.ADMIN)
    @Operation(summary = "刷新单篇文章推荐", description = "重新计算指定文章的相似推荐并写入Redis缓存")
    @PostMapping("/{id}/recommend/refresh")
    public BaseResponse<Boolean> refreshRecommend(@PathVariable String id) {
        recommendService.refreshRecommend(id);
        return ResultUtils.success(true, "推荐已刷新");
    }
}