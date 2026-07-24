package com.example.springboot.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "文章作者信息")
public class AuthorVO {

    @Schema(description = "作者ID")
    private String id;

    @Schema(description = "作者登录用户名")
    private String username;

    @Schema(description = "作者显示昵称")
    private String nickname;

    @Schema(description = "作者头像URL")
    private String avatar;

    @Schema(description = "作者个人简介")
    private String bio;
}