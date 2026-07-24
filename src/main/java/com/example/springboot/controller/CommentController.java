package com.example.springboot.controller;

import com.example.springboot.common.BaseResponse;
import com.example.springboot.common.ResultUtils;
import com.example.springboot.common.annotation.Auth;
import com.example.springboot.common.constant.RoleConstant;
import com.example.springboot.dto.CommentCreateDTO;
import com.example.springboot.dto.CommentQueryDTO;
import com.example.springboot.entity.Comments;
import com.example.springboot.service.CommentService;
import com.example.springboot.vo.CommentPageVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@Tag(name = "评论管理", description = "文章评论的增删查，支持树状嵌套结构")
@RestController
@RequestMapping
public class CommentController {

    @Resource
    private CommentService commentService;

    @Operation(summary = "获取文章评论", description = "返回树状嵌套结构的评论列表，支持分页")
    @PostMapping("/articles/{articleId}/comments/list")
    public BaseResponse<CommentPageVO> getComments(
            @Parameter(description = "文章ID") @PathVariable String articleId,
            @Valid @RequestBody CommentQueryDTO queryDTO) {
        CommentPageVO page = commentService.getCommentsByArticle(articleId, queryDTO);
        return ResultUtils.success(page);
    }

    @Operation(summary = "提交评论", description = "提交新评论或回复他人评论，支持人机校验")
    @PostMapping("/articles/{articleId}/comments")
    public BaseResponse<Comments> createComment(
            @Parameter(description = "文章ID") @PathVariable String articleId,
            @Valid @RequestBody CommentCreateDTO createDTO,
            HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        String userAgent = request.getHeader("User-Agent");
        Comments comment = commentService.createComment(articleId, createDTO, ip, userAgent);
        return ResultUtils.success(comment, "评论提交成功");
    }

    @Auth(role = RoleConstant.ADMIN)
    @Operation(summary = "删除评论", description = "管理员删除评论")
    @DeleteMapping("/comments/{id}")
    public BaseResponse<Boolean> deleteComment(@Parameter(description = "评论ID") @PathVariable String id) {
        boolean deleteComment = commentService.deleteComment(id);
        return ResultUtils.success(deleteComment, "评论删除成功");
    }
}