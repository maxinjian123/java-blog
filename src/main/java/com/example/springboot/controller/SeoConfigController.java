package com.example.springboot.controller;

import com.example.springboot.common.BaseResponse;
import com.example.springboot.common.ResultUtils;
import com.example.springboot.common.annotation.Auth;
import com.example.springboot.common.constant.RoleConstant;
import com.example.springboot.entity.SeoConfigs;
import com.example.springboot.service.SeoConfigsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "SEO配置", description = "页面SEO配置管理")
@RestController
public class SeoConfigController {

    @Resource
    private SeoConfigsService seoConfigsService;

    @Auth(role = RoleConstant.ADMIN)
    @Operation(summary = "获取指定页面的SEO配置")
    @GetMapping("/admin/seo/{pageType}")
    public BaseResponse<SeoConfigs> getSeoConfig(
            @Parameter(description = "页面类型", required = true, example = "home")
            @PathVariable String pageType) {
        SeoConfigs config = seoConfigsService.getSeoConfigByPageType(pageType);
        return ResultUtils.success(config);
    }

    @Auth(role = RoleConstant.ADMIN)
    @Operation(summary = "更新指定页面的SEO配置")
    @PutMapping("/admin/seo/{pageType}")
    public BaseResponse<Boolean> updateSeoConfig(
            @Parameter(description = "页面类型", required = true, example = "home")
            @PathVariable String pageType,
            @RequestBody SeoConfigs seoConfigs) {
        boolean updated = seoConfigsService.updateSeoConfig(pageType, seoConfigs);
        return ResultUtils.success(updated, "SEO 配置更新成功");
    }
}