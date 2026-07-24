package com.example.springboot.controller;

import com.example.springboot.common.BaseResponse;
import com.example.springboot.common.ResultUtils;
import com.example.springboot.common.annotation.Auth;
import com.example.springboot.common.constant.RoleConstant;
import com.example.springboot.dto.ConfigUpdateDTO;
import com.example.springboot.service.SysConfigService;
import com.example.springboot.vo.ConfigVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "系统配置", description = "全局公开配置与后台管理")
@RestController
public class ConfigController {

    @Resource
    private SysConfigService sysConfigService;

    @Operation(summary = "获取全局公开配置", description = "返回博客名称、公告、ICP备案等公开信息")
    @GetMapping("/config/public")
    public BaseResponse<ConfigVO> getPublicConfig() {
        ConfigVO config = sysConfigService.getPublicConfig();
        return ResultUtils.success(config);
    }

    @Auth(role = RoleConstant.ADMIN)
    @Operation(summary = "修改系统配置", description = "管理员修改系统配置项")
    @PutMapping("/admin/config")
    public BaseResponse<Boolean> updateConfig(@Valid @RequestBody ConfigUpdateDTO updateDTO) {
        sysConfigService.updateConfig(updateDTO);
        return ResultUtils.success(true, "配置更新成功");
    }
}