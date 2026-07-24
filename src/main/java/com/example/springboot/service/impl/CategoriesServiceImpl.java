package com.example.springboot.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.example.springboot.entity.Categories;
import com.example.springboot.mapper.CategoriesMapper;
import com.example.springboot.service.CategoriesService;
import org.springframework.stereotype.Service;

/**
 * 文章分类表 服务层实现。
 *
 * @author <a href="https://github.com/maxinjian123">潇潇</a>
 */
@Service
public class CategoriesServiceImpl extends ServiceImpl<CategoriesMapper, Categories>  implements CategoriesService{

}
