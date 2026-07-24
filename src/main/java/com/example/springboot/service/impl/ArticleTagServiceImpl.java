package com.example.springboot.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.example.springboot.entity.ArticleTag;
import com.example.springboot.entity.Articles;
import com.example.springboot.entity.Categories;
import com.example.springboot.mapper.ArticleTagMapper;
import com.example.springboot.mapper.ArticlesMapper;
import com.example.springboot.mapper.CategoriesMapper;
import com.example.springboot.service.ArticleTagService;
import com.example.springboot.vo.ArticleVO;
import com.example.springboot.vo.CategoryVO;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.springboot.entity.table.ArticleTagTableDef.ARTICLE_TAG;
import static com.example.springboot.entity.table.ArticlesTableDef.ARTICLES;

@Service
public class ArticleTagServiceImpl extends ServiceImpl<ArticleTagMapper, ArticleTag> implements ArticleTagService {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Resource
    private ArticleTagMapper articleTagMapper;
    @Resource
    private ArticlesMapper articlesMapper;
    @Resource
    private CategoriesMapper categoriesMapper;

    @Override
    public Page<ArticleVO> getArticlesByTag(String tagId, int page, int pageSize) {
        List<ArticleTag> relations = articleTagMapper.selectListByQuery(
                QueryWrapper.create().where(ARTICLE_TAG.TAG_ID.eq(tagId)));
        List<String> articleIds = null;
        if (CollUtil.isNotEmpty(relations)) {
            articleIds = relations.stream()
                    .map(ArticleTag::getArticleId)
                    .collect(Collectors.toList());
        }

        if (CollUtil.isEmpty(articleIds)) {
            Page<ArticleVO> emptyPage = Page.of(page, pageSize);
            emptyPage.setTotalRow(0);
            return emptyPage;
        }

        QueryWrapper qw = QueryWrapper.create()
                .where(ARTICLES.ID.in(articleIds))
                .and(ARTICLES.STATUS.eq(1))
                .orderBy(ARTICLES.CREATED_AT, false);

        Page<Articles> articlePage = articlesMapper.paginate(Page.of(page, pageSize), qw);

        List<ArticleVO> voList = articlePage.getRecords().stream()
                .map(this::buildArticleVO)
                .collect(Collectors.toList());

        Page<ArticleVO> result = Page.of(page, pageSize);
        result.setTotalRow(articlePage.getTotalRow());
        result.setRecords(voList);
        return result;
    }

    private ArticleVO buildArticleVO(Articles article) {
        Categories category = article.getCategoryId() != null
                ? categoriesMapper.selectOneById(article.getCategoryId()) : null;

        CategoryVO categoryVO = BeanUtil.copyProperties(category, CategoryVO.class);

        ArticleVO vo = BeanUtil.copyProperties(article, ArticleVO.class);
        vo.setCategory(categoryVO);
        vo.setCreatedAt(article.getCreatedAt() != null ? article.getCreatedAt().format(FORMATTER) : null);
        return vo;
    }
}