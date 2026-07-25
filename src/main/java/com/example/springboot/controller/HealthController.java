package com.example.springboot.controller;

import com.example.springboot.common.BaseResponse;
import com.example.springboot.common.ResultUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "健康检查", description = "服务健康状态检测")
@RestController
@RequestMapping("/health")
public class HealthController {

    @Operation(summary = "健康检查", description = "检测服务是否正常运行")
    @GetMapping
    public BaseResponse<String> health() {
        return ResultUtils.success("ok");
    }
}