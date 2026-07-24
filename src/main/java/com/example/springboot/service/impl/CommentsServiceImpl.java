package com.example.springboot.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.example.springboot.entity.Comments;
import com.example.springboot.mapper.CommentsMapper;
import com.example.springboot.service.CommentsService;
import org.springframework.stereotype.Service;

/**
 * 文章评论表 服务层实现。
 *
 * @author <a href="https://github.com/maxinjian123">潇潇</a>
 */
@Service
public class CommentsServiceImpl extends ServiceImpl<CommentsMapper, Comments>  implements CommentsService{

}
