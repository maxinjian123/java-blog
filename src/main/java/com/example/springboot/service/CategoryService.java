package com.example.springboot.service;

import com.example.springboot.dto.CategoryCreateDTO;
import com.example.springboot.dto.CategoryUpdateDTO;
import com.example.springboot.entity.Categories;
import com.example.springboot.vo.CategoryWithCountVO;
import com.mybatisflex.core.service.IService;

import java.util.List;

public interface CategoryService extends IService<Categories> {

    List<CategoryWithCountVO> listCategories();

    CategoryWithCountVO createCategory(CategoryCreateDTO createDTO);

    boolean updateCategory(String id, CategoryUpdateDTO updateDTO);

    boolean deleteCategory(String id);
}