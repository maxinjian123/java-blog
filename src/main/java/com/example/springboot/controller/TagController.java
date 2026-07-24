package com.example.springboot.controller;

import com.example.springboot.common.BaseResponse;
import com.example.springboot.common.ResultUtils;
import com.example.springboot.common.annotation.Auth;
import com.example.springboot.common.constant.RoleConstant;
import com.example.springboot.dto.TagCreateDTO;
import com.example.springboot.dto.TagUpdateDTO;
import com.example.springboot.entity.Tags;
import com.example.springboot.service.TagService;
import com.example.springboot.vo.ArticleVO;
import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "标签管理", description = "文章标签的增删改查及文章关联")
@RestController
@RequestMapping("/tags")
public class TagController {

    @Resource
    private TagService tagService;

    @Operation(summary = "获取标签云数据", description = "返回所有标签列表")
    @GetMapping
    public BaseResponse<List<Tags>> getTags() {
        List<Tags> tags = tagService.listTags();
        return ResultUtils.success(tags);
    }

    @Auth(role = RoleConstant.ADMIN)
    @Operation(summary = "新建标签", description = "管理员创建新标签")
    @PostMapping
    public BaseResponse<Tags> createTag(@Valid @RequestBody TagCreateDTO createDTO) {
        Tags tag = tagService.createTag(createDTO);
        return ResultUtils.success(tag, "标签创建成功");
    }

    @Auth(role = RoleConstant.ADMIN)
    @Operation(summary = "修改标签", description = "管理员修改标签名称和别名")
    @PutMapping("/{id}")
    public BaseResponse<Tags> updateTag(
            @Parameter(description = "标签ID") @PathVariable String id,
            @Valid @RequestBody TagUpdateDTO updateDTO) {
        Tags tag = tagService.updateTag(id, updateDTO);
        return ResultUtils.success(tag, "标签更新成功");
    }

    @Auth(role = RoleConstant.ADMIN)
    @Operation(summary = "删除标签", description = "管理员删除标签，同时清除关联关系")
    @DeleteMapping("/{id}")
    public BaseResponse<Boolean> deleteTag(@Parameter(description = "标签ID") @PathVariable String id) {
        boolean deleted = tagService.deleteTag(id);
        return ResultUtils.success(deleted, "标签删除成功");
    }

    @Operation(summary = "关键字查询标签", description = "根据关键字模糊搜索标签名称")
    @GetMapping("/search")
    public BaseResponse<List<Tags>> searchTags(
            @Parameter(description = "搜索关键字") @RequestParam String keyword) {
        List<Tags> tags = tagService.searchTags(keyword);
        return ResultUtils.success(tags);
    }

}