package com.example.springboot.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "发表评论请求")
public class CommentCreateDTO {

    @NotBlank(message = "评论昵称不能为空")
    @Schema(description = "评论者昵称", required = true)
    private String nickname;

    @Email(message = "邮箱格式不正确")
    @Schema(description = "评论者邮箱")
    private String email;

    @Schema(description = "个人网站")
    private String website;

    @NotBlank(message = "评论内容不能为空")
    @Schema(description = "评论内容", required = true)
    private String content;

    @Schema(description = "父评论ID，回复他人时传入")
    private String parentId;

    @Schema(description = "人机校验Token")
    private String captchaToken;
}