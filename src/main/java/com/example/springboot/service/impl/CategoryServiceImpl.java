package com.example.springboot.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.example.springboot.common.exception.BusinessException;
import com.example.springboot.common.exception.ErrorCode;
import com.example.springboot.dto.CategoryCreateDTO;
import com.example.springboot.dto.CategoryUpdateDTO;
import com.example.springboot.entity.Categories;
import com.example.springboot.mapper.ArticlesMapper;
import com.example.springboot.mapper.CategoriesMapper;
import com.example.springboot.service.CategoryService;
import com.example.springboot.vo.CategoryWithCountVO;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.springboot.entity.table.ArticlesTableDef.ARTICLES;
import static com.example.springboot.entity.table.CategoriesTableDef.CATEGORIES;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoriesMapper, Categories> implements CategoryService {

    private static final Logger log = LoggerFactory.getLogger(CategoryServiceImpl.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final ArticlesMapper articlesMapper;

    public CategoryServiceImpl(ArticlesMapper articlesMapper) {
        this.articlesMapper = articlesMapper;
    }

    @Override
    public List<CategoryWithCountVO> listCategories() {
        List<Categories> categories = list(QueryWrapper.create().orderBy(CATEGORIES.CREATED_AT, true));

        return categories.stream().map(category -> {
            long articleCount = countArticlesByCategory(category.getId());
            CategoryWithCountVO vo = BeanUtil.copyProperties(category, CategoryWithCountVO.class);
            vo.setArticleCount(articleCount);
            vo.setCreatedAt(category.getCreatedAt() != null ? category.getCreatedAt().format(FORMATTER) : null);
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CategoryWithCountVO createCategory(CategoryCreateDTO createDTO) {
        Categories category = BeanUtil.copyProperties(createDTO, Categories.class);
        category.setArticleCount(0L);
        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());

        save(category);
        log.info("Category created: id={}, name={}", category.getId(), category.getName());

        CategoryWithCountVO vo = BeanUtil.copyProperties(category, CategoryWithCountVO.class);
        vo.setArticleCount(0L);
        vo.setCreatedAt(category.getCreatedAt().format(FORMATTER));
        return vo;
    }

    @Override
    @Transactional
    public boolean updateCategory(String id, CategoryUpdateDTO updateDTO) {
        Categories category = getById(id);
        if (category == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "分类不存在");
        }

        BeanUtil.copyProperties(updateDTO, category, true);
        category.setUpdatedAt(LocalDateTime.now());
        updateById(category);
        log.info("Category updated: id={}", id);
        return true;
    }

    @Override
    @Transactional
    public boolean deleteCategory(String id) {
        Categories category = getById(id);
        if (category == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "分类不存在");
        }

        removeById(id);
        log.info("Category deleted: id={}", id);
        return true;
    }

    private long countArticlesByCategory(String categoryId) {
        return articlesMapper.selectCountByQuery(
                QueryWrapper.create().where(ARTICLES.CATEGORY_ID.eq(categoryId)));
    }
}