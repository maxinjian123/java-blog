package com.example.springboot.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.example.springboot.common.exception.BusinessException;
import com.example.springboot.common.exception.ErrorCode;
import com.example.springboot.common.exception.ThrowUtils;
import com.example.springboot.dto.CommentCreateDTO;
import com.example.springboot.dto.CommentQueryDTO;
import com.example.springboot.entity.Comments;
import com.example.springboot.mapper.CommentsMapper;
import com.example.springboot.service.CommentService;
import com.example.springboot.service.TrendingService;
import com.example.springboot.vo.CommentPageVO;
import com.example.springboot.vo.CommentVO;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.example.springboot.entity.table.CommentsTableDef.COMMENTS;

@Service
public class CommentServiceImpl extends ServiceImpl<CommentsMapper, Comments> implements CommentService {

    private static final Logger log = LoggerFactory.getLogger(CommentServiceImpl.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final int APPROVED = 1;

    private final TrendingService trendingService;

    public CommentServiceImpl(TrendingService trendingService) {
        this.trendingService = trendingService;
    }

    @Override
    public CommentPageVO getCommentsByArticle(String articleId, CommentQueryDTO queryDTO) {
        List<Comments> allComments = list(QueryWrapper.create()
                .where(COMMENTS.ARTICLE_ID.eq(articleId))
                .and(COMMENTS.STATUS.eq(APPROVED))
                .orderBy(COMMENTS.CREATED_AT, true));

        Map<String, List<Comments>> groupedByParent = allComments.stream()
                .collect(Collectors.groupingBy(c -> StrUtil.emptyToDefault(c.getParentId(), StrUtil.EMPTY)));

        List<Comments> topLevelComments = groupedByParent.getOrDefault(StrUtil.EMPTY, Collections.emptyList());

        long total = topLevelComments.size();
        int page = queryDTO.getPage();
        int pageSize = queryDTO.getPageSize();
        int fromIndex = (page - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, topLevelComments.size());

        List<Comments> pagedTopLevel;
        if (fromIndex >= topLevelComments.size()) {
            pagedTopLevel = Collections.emptyList();
        } else {
            pagedTopLevel = topLevelComments.subList(fromIndex, toIndex);
        }

        List<CommentVO> voList = pagedTopLevel.stream()
                .map(comment -> buildCommentTree(comment, groupedByParent))
                .collect(Collectors.toList());

        return CommentPageVO.builder()
                .total(total)
                .page(page)
                .pageSize(pageSize)
                .list(voList)
                .build();
    }

    @Override
    @Transactional
    public Comments createComment(String articleId, CommentCreateDTO createDTO, String ip, String userAgent) {
        Comments comment = BeanUtil.copyProperties(createDTO, Comments.class);
        comment.setArticleId(articleId);
        comment.setEmail(ObjectUtil.defaultIfNull(createDTO.getEmail(), StrUtil.EMPTY));
        comment.setWebsite(ObjectUtil.defaultIfNull(createDTO.getWebsite(), StrUtil.EMPTY));
        comment.setIp(ObjectUtil.defaultIfNull(ip, StrUtil.EMPTY));
        comment.setUserAgent(ObjectUtil.defaultIfNull(userAgent, StrUtil.EMPTY));
        comment.setIsAdmin(false);
        comment.setStatus(APPROVED);
        comment.setCreatedAt(LocalDateTime.now());

        save(comment);
        if (ObjectUtil.equal(APPROVED, comment.getStatus()) && StrUtil.isNotBlank(articleId)) {
            trendingService.incrementComment(articleId);
        }
        log.info("Comment created: id={}, articleId={}", comment.getId(), articleId);
        return comment;
    }

    @Override
    @Transactional
    public boolean deleteComment(String id) {
        Comments comment = getById(id);
        if (comment == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "评论不存在");
        }

        String pathPrefix = StrUtil.emptyToDefault(comment.getPath(), StrUtil.EMPTY) + id + "/";
        List<Comments> childComments = list(QueryWrapper.create()
                .where(COMMENTS.PATH.like(pathPrefix + "%")));
        if (CollUtil.isNotEmpty(childComments)) {
            List<String> childIds = childComments.stream()
                    .map(Comments::getId)
                    .collect(Collectors.toList());
            removeByIds(childIds);
            log.info("Cascade deleted {} child comments for parent id={}", childIds.size(), id);
        }

        boolean removed = removeById(id);
        ThrowUtils.throwIf(!removed, ErrorCode.OPERATION_ERROR, "评论删除失败");
        log.info("Comment deleted: id={}", id);
        return true;
    }

    private CommentVO buildCommentTree(Comments comment, Map<String, List<Comments>> groupedByParent) {
        List<Comments> children = groupedByParent.getOrDefault(comment.getId(), Collections.emptyList());

        List<CommentVO> replyVOs = children.stream()
                .map(child -> buildCommentTree(child, groupedByParent))
                .collect(Collectors.toList());

        CommentVO vo = BeanUtil.copyProperties(comment, CommentVO.class);
        vo.setCreatedAt(comment.getCreatedAt() != null ? comment.getCreatedAt().format(FORMATTER) : null);
        vo.setReplies(CollUtil.isEmpty(replyVOs) ? null : replyVOs);
        return vo;
    }
}