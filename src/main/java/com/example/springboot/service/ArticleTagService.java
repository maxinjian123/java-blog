package com.example.springboot.service;

import com.example.springboot.vo.ArticleVO;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import com.example.springboot.entity.ArticleTag;

/**
 * 文章与标签关联表 服务层。
 *
 * @author <a href="https://github.com/maxinjian123">潇潇</a>
 */
public interface ArticleTagService extends IService<ArticleTag> {

    Page<ArticleVO> getArticlesByTag(String tagId, int page, int pageSize);

}
