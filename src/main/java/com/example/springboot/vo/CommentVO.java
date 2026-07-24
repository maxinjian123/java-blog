package com.example.springboot.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "评论信息（支持树形嵌套回复）")
public class CommentVO {

    @Schema(description = "评论ID")
    private String id;

    @Schema(description = "评论者昵称")
    private String nickname;

    @Schema(description = "评论者邮箱MD5，用于生成Gravatar头像")
    private String emailMd5;

    @Schema(description = "评论正文内容")
    private String content;

    @Schema(description = "评论者个人网站URL")
    private String website;

    @Schema(description = "是否为管理员发布的评论")
    private Boolean isAdmin;

    @Schema(description = "父级评论ID，顶级评论为null")
    private String parentId;

    @Schema(description = "评论时间 yyyy-MM-dd HH:mm:ss")
    private String createdAt;

    @Schema(description = "子回复列表，按时间正序排列")
    private List<CommentVO> replies;
}