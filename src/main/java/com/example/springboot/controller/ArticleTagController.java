package com.example.springboot.controller;

import com.example.springboot.common.BaseResponse;
import com.example.springboot.common.ResultUtils;
import com.example.springboot.vo.ArticleVO;
import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.springboot.entity.ArticleTag;
import com.example.springboot.service.ArticleTagService;

import java.util.List;

/**
 * 文章与标签关联表 控制层。
 *
 * @author <a href="https://github.com/maxinjian123">潇潇</a>
 */
@RestController
@RequestMapping("/articleTag")
@Tag(name = "文章与标签关联", description = "文章与标签关联表相关接口")
public class ArticleTagController {

    @Resource
    private ArticleTagService articleTagService;

    @Operation(summary = "标签查询文章", description = "根据标签ID分页查询关联文章")
    @GetMapping("/{id}/articles")
    public BaseResponse<Page<ArticleVO>> getArticlesByTag(
            @Parameter(description = "标签ID") @PathVariable String id,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int pageSize) {
        Page<ArticleVO> result = articleTagService.getArticlesByTag(id, page, pageSize);
        return ResultUtils.success(result);
    }
}
