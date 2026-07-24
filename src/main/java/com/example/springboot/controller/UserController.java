package com.example.springboot.controller;

import com.example.springboot.common.BaseResponse;
import com.example.springboot.common.ResultUtils;
import com.example.springboot.common.UserContext;
import com.example.springboot.common.annotation.Auth;
import com.example.springboot.dto.UserProfileUpdateDTO;
import com.example.springboot.service.UserService;
import com.example.springboot.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "用户管理", description = "当前登录用户信息管理")
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @Auth
    @Operation(summary = "获取当前用户信息", description = "从Session中获取当前登录用户的详细信息")
    @GetMapping("/profile")
    public BaseResponse<UserVO> getProfile() {
        String userId = UserContext.getCurrentUserId();
        UserVO userVO = userService.getUserProfile(userId);
        return ResultUtils.success(userVO);
    }

    @Auth
    @Operation(summary = "更新当前用户信息", description = "更新当前登录用户的昵称、头像、简介、邮箱等信息")
    @PutMapping("/profile")
    public BaseResponse<Boolean> updateProfile(@Valid @RequestBody UserProfileUpdateDTO profileDTO) {
        String userId = UserContext.getCurrentUserId();
        boolean updated = userService.updateUserProfile(userId, profileDTO);
        return ResultUtils.success(updated, "更新成功");
    }
}