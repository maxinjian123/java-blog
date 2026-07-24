package com.example.springboot.controller;

import com.example.springboot.common.BaseResponse;
import com.example.springboot.common.ResultUtils;
import com.example.springboot.common.UserContext;
import com.example.springboot.common.annotation.Auth;
import com.example.springboot.common.constant.RoleConstant;
import com.example.springboot.dto.ArticleCreateDTO;
import com.example.springboot.dto.ArticleQueryDTO;
import com.example.springboot.dto.ArticleUpdateDTO;
import com.example.springboot.service.ArticleService;
import com.example.springboot.vo.ArticleDetailVO;
import com.example.springboot.vo.ArticleVO;
import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "文章管理", description = "文章列表、详情、创建、更新、删除、点赞")
@RestController
@RequestMapping("/articles")
public class ArticleController {

    @Resource
    private ArticleService articleService;

    @Operation(summary = "获取文章列表", description = "支持多维度筛选（分类、标签、状态）与游标分页")
    @PostMapping("/page")
    public BaseResponse<Page<ArticleVO>> getArticles(@Valid @RequestBody ArticleQueryDTO queryDTO) {
        Page<ArticleVO> page = articleService.getArticles(queryDTO);
        return ResultUtils.success(page);
    }

    @Operation(summary = "获取文章详情", description = "支持通过文章ID或URL别名查询完整详情")
    @GetMapping("/{idOrSlug}")
    public BaseResponse<ArticleDetailVO> getArticle(
            @Parameter(description = "文章ID或URL别名") @PathVariable String idOrSlug) {
        ArticleDetailVO detail = articleService.getArticleByIdOrSlug(idOrSlug);
        return ResultUtils.success(detail);
    }

    @Auth(role = RoleConstant.ADMIN)
    @Operation(summary = "新建文章", description = "管理员创建新文章")
    @PostMapping
    public BaseResponse<ArticleDetailVO> createArticle(@Valid @RequestBody ArticleCreateDTO createDTO) {
        String userId = UserContext.getCurrentUserId();
        ArticleDetailVO detail = articleService.createArticle(createDTO, userId);
        return ResultUtils.success(detail, "文章创建成功");
    }

    @Auth(role = RoleConstant.ADMIN)
    @Operation(summary = "更新文章", description = "管理员更新文章")
    @PutMapping("/{id}")
    public BaseResponse<Boolean> updateArticle(
            @Parameter(description = "文章ID") @PathVariable String id,
            @Valid @RequestBody ArticleUpdateDTO updateDTO) {
        String userId = UserContext.getCurrentUserId();
        boolean b = articleService.updateArticle(id, updateDTO, userId);
        return ResultUtils.success(b, "文章更新成功");
    }

    @Auth(role = RoleConstant.ADMIN)
    @Operation(summary = "删除文章", description = "管理员删除文章")
    @DeleteMapping("/{id}")
    public BaseResponse<Boolean> deleteArticle(@Parameter(description = "文章ID") @PathVariable String id) {
        boolean b = articleService.deleteArticle(id);
        return ResultUtils.success(b, "文章删除成功");
    }

    @Operation(summary = "文章点赞", description = "基于IP和Cookie的24小时防刷限制")
    @PostMapping("/{id}/like")
    public BaseResponse<Boolean> likeArticle(
            @Parameter(description = "文章ID") @PathVariable String id,
            HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        String cookie = request.getHeader("Cookie");
        boolean liked = articleService.likeArticle(id, ip, cookie);
        return ResultUtils.success(liked, "点赞成功");
    }
}