package com.example.springboot.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.example.springboot.entity.Tags;
import com.example.springboot.mapper.TagsMapper;
import com.example.springboot.service.TagsService;
import org.springframework.stereotype.Service;

/**
 * 文章标签表 服务层实现。
 *
 * @author <a href="https://github.com/maxinjian123">潇潇</a>
 */
@Service
public class TagsServiceImpl extends ServiceImpl<TagsMapper, Tags>  implements TagsService{

}
