package com.example.springboot.service;

import com.example.springboot.dto.CommentCreateDTO;
import com.example.springboot.dto.CommentQueryDTO;
import com.example.springboot.entity.Comments;
import com.example.springboot.vo.CommentPageVO;
import com.mybatisflex.core.service.IService;

public interface CommentService extends IService<Comments> {

    CommentPageVO getCommentsByArticle(String articleId, CommentQueryDTO queryDTO);

    Comments createComment(String articleId, CommentCreateDTO createDTO, String ip, String userAgent);

    boolean deleteComment(String id);
}