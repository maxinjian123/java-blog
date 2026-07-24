package com.example.springboot.controller;

import com.example.springboot.common.BaseResponse;
import com.example.springboot.common.ResultUtils;
import com.example.springboot.dto.SearchDTO;
import com.example.springboot.service.ArticleService;
import com.example.springboot.vo.SearchPageVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "全文搜索", description = "支持标题和正文高亮搜索")
@RestController
public class SearchController {

    @Resource
    private ArticleService articleService;

    @Operation(summary = "全文搜索", description = "根据关键词搜索文章标题和正文，返回高亮匹配结果")
    @PostMapping("/search")
    public BaseResponse<SearchPageVO> search(@Valid @RequestBody SearchDTO searchDTO) {
        SearchPageVO result = articleService.search(searchDTO);
        return ResultUtils.success(result);
    }
}