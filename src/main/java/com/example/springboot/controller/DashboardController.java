package com.example.springboot.controller;

import com.example.springboot.common.BaseResponse;
import com.example.springboot.common.ResultUtils;
import com.example.springboot.common.annotation.Auth;
import com.example.springboot.common.constant.RoleConstant;
import com.example.springboot.service.ArticleService;
import com.example.springboot.vo.DashboardVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "仪表盘", description = "后台管理仪表盘数据统计")
@RestController
@RequestMapping("/admin")
public class DashboardController {

    @Resource
    private ArticleService articleService;

    @Auth(role = RoleConstant.ADMIN)
    @Operation(summary = "获取仪表盘数据", description = "返回文章总数、评论总数、PV/UV、最新评论等")
    @GetMapping("/dashboard/stats")
    public BaseResponse<DashboardVO> getDashboardStats() {
        DashboardVO stats = articleService.getDashboardStats();
        return ResultUtils.success(stats);
    }
}