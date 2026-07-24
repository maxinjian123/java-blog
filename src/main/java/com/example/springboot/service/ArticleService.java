package com.example.springboot.service;

import com.example.springboot.dto.ArticleCreateDTO;
import com.example.springboot.dto.ArticleQueryDTO;
import com.example.springboot.dto.ArticleUpdateDTO;
import com.example.springboot.dto.SearchDTO;
import com.example.springboot.entity.Articles;
import com.example.springboot.vo.ArticleDetailVO;
import com.example.springboot.vo.ArticleVO;
import com.example.springboot.vo.DashboardVO;
import com.example.springboot.vo.SearchPageVO;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;

public interface ArticleService extends IService<Articles> {

    Page<ArticleVO> getArticles(ArticleQueryDTO queryDTO);

    ArticleDetailVO getArticleByIdOrSlug(String idOrSlug);

    ArticleDetailVO createArticle(ArticleCreateDTO createDTO, String userId);

    boolean updateArticle(String id, ArticleUpdateDTO updateDTO, String userId);

    boolean deleteArticle(String id);

    boolean likeArticle(String articleId, String ip, String cookie);

    SearchPageVO search(SearchDTO searchDTO);

    DashboardVO getDashboardStats();
}