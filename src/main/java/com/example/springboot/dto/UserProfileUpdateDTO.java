package com.example.springboot.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "更新用户信息请求")
public class UserProfileUpdateDTO {

    @Schema(description = "显示昵称", example = "新昵称")
    private String nickname;

    @Schema(description = "头像URL", example = "https://cdn.com/a.jpg")
    private String avatar;

    @Schema(description = "个人简介", example = "个人简介")
    private String bio;

    @Schema(description = "邮箱", example = "admin@blog.com")
    private String email;
}