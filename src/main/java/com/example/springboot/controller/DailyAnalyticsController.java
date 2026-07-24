package com.example.springboot.controller;

import com.example.springboot.common.BaseResponse;
import com.example.springboot.common.ResultUtils;
import com.example.springboot.common.annotation.Auth;
import com.example.springboot.common.constant.RoleConstant;
import com.example.springboot.service.DailyAnalyticsService;
import com.example.springboot.vo.TrendVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "访问统计", description = "后台访问趋势数据")
@RestController
@RequestMapping("/admin/analytics")
public class DailyAnalyticsController {

    @Resource
    private DailyAnalyticsService dailyAnalyticsService;

    @Auth(role = RoleConstant.ADMIN)
    @Operation(summary = "获取近期访问趋势数据", description = "获取最近N天的PV/UV趋势，默认7天，最大30天")
    @GetMapping("/trend")
    public BaseResponse<List<TrendVO>> getTrend(
            @Parameter(description = "最近N天，默认7天") @RequestParam(defaultValue = "7") int days) {
        List<TrendVO> trend = dailyAnalyticsService.getTrend(days);
        return ResultUtils.success(trend);
    }
}