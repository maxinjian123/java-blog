package com.example.springboot.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.example.springboot.entity.Articles;
import com.example.springboot.mapper.ArticlesMapper;
import com.example.springboot.service.ArticlesService;
import org.springframework.stereotype.Service;

/**
 * 文章主表 服务层实现。
 *
 * @author <a href="https://github.com/maxinjian123">潇潇</a>
 */
@Service
public class ArticlesServiceImpl extends ServiceImpl<ArticlesMapper, Articles>  implements ArticlesService{

}
