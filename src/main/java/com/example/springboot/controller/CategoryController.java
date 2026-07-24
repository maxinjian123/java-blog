package com.example.springboot.controller;

import com.example.springboot.common.BaseResponse;
import com.example.springboot.common.ResultUtils;
import com.example.springboot.common.annotation.Auth;
import com.example.springboot.common.constant.RoleConstant;
import com.example.springboot.dto.CategoryCreateDTO;
import com.example.springboot.dto.CategoryUpdateDTO;
import com.example.springboot.service.CategoryService;
import com.example.springboot.vo.CategoryWithCountVO;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "分类管理", description = "文章分类的增删改查")
@RestController
@RequestMapping("/categories")
public class CategoryController {

    @Resource
    private CategoryService categoryService;

    @Operation(summary = "获取所有分类列表", description = "返回所有分类及各分类下文章计数")
    @GetMapping
    public BaseResponse<List<CategoryWithCountVO>> getCategories() {
        List<CategoryWithCountVO> categories = categoryService.listCategories();
        return ResultUtils.success(categories);
    }

    @Auth(role = RoleConstant.ADMIN)
    @Operation(summary = "新建分类", description = "管理员创建新分类")
    @PostMapping
    public BaseResponse<CategoryWithCountVO> createCategory(@Valid @RequestBody CategoryCreateDTO createDTO) {
        CategoryWithCountVO category = categoryService.createCategory(createDTO);
        return ResultUtils.success(category, "分类创建成功");
    }

    @Auth(role = RoleConstant.ADMIN)
    @Operation(summary = "修改分类", description = "管理员修改分类信息")
    @PutMapping("/{id}")
    public BaseResponse<Boolean> updateCategory(
            @Parameter(description = "分类ID") @PathVariable String id,
            @Valid @RequestBody CategoryUpdateDTO updateDTO) {
        categoryService.updateCategory(id, updateDTO);
        return ResultUtils.success(true, "分类更新成功");
    }

    @Auth(role = RoleConstant.ADMIN)
    @Operation(summary = "删除分类", description = "管理员删除分类")
    @DeleteMapping("/{id}")
    public BaseResponse<Boolean> deleteCategory(@Parameter(description = "分类ID") @PathVariable String id) {
        categoryService.deleteCategory(id);
        return ResultUtils.success(true, "分类删除成功");
    }
}