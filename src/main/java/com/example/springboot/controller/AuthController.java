package com.example.springboot.controller;

import com.example.springboot.common.BaseResponse;
import com.example.springboot.common.ResultUtils;
import com.example.springboot.common.UserContext;
import com.example.springboot.dto.LoginDTO;
import com.example.springboot.entity.User;
import com.example.springboot.service.UserService;
import com.example.springboot.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "认证管理", description = "用户登录、登出")
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Resource
    private UserService userService;

    @Operation(summary = "用户登录", description = "使用用户名和密码登录，Session存储在Redis中")
    @PostMapping("/login")
    public BaseResponse<UserVO> login(@Valid @RequestBody LoginDTO loginDTO, HttpSession session) {
        UserVO userVO = userService.login(loginDTO);
        User currentUser = userService.getById(userVO.getId());
        if (currentUser != null) {
            currentUser.setPassword(null);
        }
        session.setAttribute("currentUser", currentUser);
        return ResultUtils.success(userVO, "登录成功");
    }
}