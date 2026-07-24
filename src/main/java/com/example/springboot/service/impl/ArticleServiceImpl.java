package com.example.springboot.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.example.springboot.common.exception.BusinessException;
import com.example.springboot.common.exception.ErrorCode;
import com.example.springboot.common.exception.ThrowUtils;
import com.example.springboot.dto.ArticleCreateDTO;
import com.example.springboot.dto.ArticleQueryDTO;
import com.example.springboot.dto.ArticleUpdateDTO;
import com.example.springboot.dto.SearchDTO;
import com.example.springboot.entity.ArticleTag;
import com.example.springboot.entity.Articles;
import com.example.springboot.entity.Categories;
import com.example.springboot.entity.Comments;
import com.example.springboot.entity.Tags;
import com.example.springboot.entity.User;
import com.example.springboot.mapper.ArticleTagMapper;
import com.example.springboot.mapper.ArticlesMapper;
import com.example.springboot.mapper.CategoriesMapper;
import com.example.springboot.mapper.CommentsMapper;
import com.example.springboot.mapper.TagsMapper;
import com.example.springboot.mapper.UserMapper;
import com.example.springboot.service.ArticleService;
import com.example.springboot.service.RecommendService;
import com.example.springboot.service.TrendingService;
import com.example.springboot.util.RedisUtil;
import com.example.springboot.vo.ArticleDetailVO;
import com.example.springboot.vo.ArticleVO;
import com.example.springboot.vo.AuthorVO;
import com.example.springboot.vo.CategoryVO;
import com.example.springboot.vo.CommentVO;
import com.example.springboot.vo.DashboardVO;
import com.example.springboot.vo.SearchPageVO;
import com.example.springboot.vo.SearchVO;
import com.example.springboot.vo.TagVO;
import com.mybatisflex.core.paginate.Page;
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
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.example.springboot.entity.table.ArticlesTableDef.ARTICLES;
import static com.example.springboot.entity.table.ArticleTagTableDef.ARTICLE_TAG;
import static com.example.springboot.entity.table.CommentsTableDef.COMMENTS;

@Service
public class ArticleServiceImpl extends ServiceImpl<ArticlesMapper, Articles> implements ArticleService {

    private static final Logger log = LoggerFactory.getLogger(ArticleServiceImpl.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String LIKE_LIMIT_PREFIX = "article:like:";
    private static final Integer STATUS_PUBLISHED = 1;

    private final ArticleTagMapper articleTagMapper;
    private final CommentsMapper commentsMapper;
    private final UserMapper userMapper;
    private final CategoriesMapper categoriesMapper;
    private final TagsMapper tagsMapper;
    private final RedisUtil redisUtil;
    private final TrendingService trendingService;
    private final RecommendService recommendService;

    public ArticleServiceImpl(ArticleTagMapper articleTagMapper, CommentsMapper commentsMapper,
                              UserMapper userMapper, CategoriesMapper categoriesMapper,
                              TagsMapper tagsMapper, RedisUtil redisUtil,
                              TrendingService trendingService, RecommendService recommendService) {
        this.articleTagMapper = articleTagMapper;
        this.commentsMapper = commentsMapper;
        this.userMapper = userMapper;
        this.categoriesMapper = categoriesMapper;
        this.tagsMapper = tagsMapper;
        this.redisUtil = redisUtil;
        this.trendingService = trendingService;
        this.recommendService = recommendService;
    }

    @Override
    public Page<ArticleVO> getArticles(ArticleQueryDTO queryDTO) {
        QueryWrapper qw = QueryWrapper.create()
                .select(ARTICLES.ID, ARTICLES.TITLE, ARTICLES.SLUG, ARTICLES.SUMMARY,
                        ARTICLES.COVER, ARTICLES.VIEWS, ARTICLES.LIKES, ARTICLES.CATEGORY_ID,
                        ARTICLES.CREATED_AT)
                .from(ARTICLES);

        if (queryDTO.getStatus() != null) {
            qw.and(ARTICLES.STATUS.eq(queryDTO.getStatus()));
        }

        if (StrUtil.isNotBlank(queryDTO.getCategoryId())) {
            qw.and(ARTICLES.CATEGORY_ID.eq(queryDTO.getCategoryId()));
        }

        if (StrUtil.isNotBlank(queryDTO.getTagId())) {
            qw.and(ARTICLES.ID.in(
                    QueryWrapper.create()
                            .select(ARTICLE_TAG.ARTICLE_ID)
                            .from(ARTICLE_TAG)
                            .where(ARTICLE_TAG.TAG_ID.eq(queryDTO.getTagId()))
            ));
        }

        if (StrUtil.isNotBlank(queryDTO.getLastId())) {
            qw.and(ARTICLES.ID.lt(queryDTO.getLastId()));
        }

        qw.orderBy(ARTICLES.CREATED_AT, false);

        Page<Articles> page = page(Page.of(queryDTO.getPage(), queryDTO.getPageSize()), qw);

        List<ArticleVO> voList = page.getRecords().stream()
                .map(this::buildArticleVO)
                .collect(Collectors.toList());

        batchFillCommentCounts(voList);

        Page<ArticleVO> result = new Page<>();
        result.setTotalRow(page.getTotalRow());
        result.setPageNumber(page.getPageNumber());
        result.setPageSize(page.getPageSize());
        result.setRecords(voList);
        return result;
    }

    @Override
    public ArticleDetailVO getArticleByIdOrSlug(String idOrSlug) {
        Articles article = getById(idOrSlug);
        if (article == null) {
            article = getOne(QueryWrapper.create().where(ARTICLES.SLUG.eq(idOrSlug)));
        }

        if (article == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "文章不存在");
        }

        trendingService.incrementView(article.getId());
        Articles update = new Articles();
        update.setId(article.getId());
        update.setViews(ObjectUtil.defaultIfNull(article.getViews(), 0L) + 1);
        updateById(update);
        article.setViews(update.getViews());

        return buildArticleDetailVO(article);
    }

    @Override
    @Transactional
    public ArticleDetailVO createArticle(ArticleCreateDTO createDTO, String userId) {
        Articles article = BeanUtil.copyProperties(createDTO, Articles.class);
        article.setAuthorId(userId);
        article.setViews(0L);
        article.setLikes(0L);
        article.setCreatedAt(LocalDateTime.now());
        article.setUpdatedAt(LocalDateTime.now());

        save(article);

        if (CollUtil.isNotEmpty(createDTO.getTagIds())) {
            saveArticleTags(article.getId(), createDTO.getTagIds());
        }

        if (ObjectUtil.equal(STATUS_PUBLISHED, article.getStatus())) {
            trendingService.rebuildTrending();
            recommendService.refreshRecommend(article.getId());
        }

        log.info("Article created: id={}, userId={}", article.getId(), userId);
        return buildArticleDetailVO(article);
    }

    @Override
    @Transactional
    public boolean updateArticle(String id, ArticleUpdateDTO updateDTO, String userId) {
        Articles article = getById(id);
        if (article == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "文章不存在");
        }

        BeanUtil.copyProperties(updateDTO, article, true);
        article.setUpdatedAt(LocalDateTime.now());
        updateById(article);
        handleTagUpdates(id, updateDTO.getTagIds());

        if (ObjectUtil.equal(STATUS_PUBLISHED, article.getStatus())) {
            recommendService.refreshRecommend(article.getId());
        }
        trendingService.rebuildTrending();

        log.info("Article updated: id={}, userId={}", id, userId);
        return true;
    }

    private void handleTagUpdates(String articleId, List<String> tagIds) {
        if (tagIds == null) {
            return;
        }
        articleTagMapper.deleteByQuery(QueryWrapper.create().where(ARTICLE_TAG.ARTICLE_ID.eq(articleId)));
        if (CollUtil.isNotEmpty(tagIds)) {
            saveArticleTags(articleId, tagIds);
        }
    }

    @Override
    @Transactional
    public boolean deleteArticle(String id) {
        Articles article = getById(id);
        if (article == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "文章不存在");
        }

        boolean removed = removeById(id);
        ThrowUtils.throwIf(!removed, ErrorCode.OPERATION_ERROR, "文章删除失败");
        int byQuery = articleTagMapper.deleteByQuery(QueryWrapper.create().where(ARTICLE_TAG.ARTICLE_ID.eq(id)));
        ThrowUtils.throwIf(byQuery < 0, ErrorCode.OPERATION_ERROR, "文章-标签关联数据删除失败");
        trendingService.removeFromTrending(id);
        recommendService.removeRecommend(id);
        log.info("Article deleted: id={}", id);
        return true;
    }

    @Override
    public boolean likeArticle(String articleId, String ip, String cookie) {
        Articles article = getById(articleId);
        if (article == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "文章不存在");
        }

        String key = LIKE_LIMIT_PREFIX + articleId + ":" + ObjectUtil.defaultIfNull(ip, cookie);
        if (Boolean.TRUE.equals(redisUtil.setIfAbsent(key, "1", 24, TimeUnit.HOURS))) {
            Articles update = new Articles();
            update.setId(articleId);
            update.setLikes(ObjectUtil.defaultIfNull(article.getLikes(), 0L) + 1);
            updateById(update);
            trendingService.incrementLike(articleId);
            return true;
        }

        throw new BusinessException(ErrorCode.OPERATION_ERROR, "您已经点过赞了");
    }

    private void saveArticleTags(String articleId, List<String> tagIds) {
        List<ArticleTag> articleTags = tagIds.stream()
                .map(tagId -> ArticleTag.builder().articleId(articleId).tagId(tagId).build())
                .collect(Collectors.toList());
        articleTagMapper.insertBatch(articleTags);
    }

    private ArticleVO buildArticleVO(Articles article) {
        Categories category = article.getCategoryId() != null
                ? categoriesMapper.selectOneById(article.getCategoryId()) : null;
        List<Tags> tags = getTagsByArticleId(article.getId());

        CategoryVO categoryVO = BeanUtil.copyProperties(category, CategoryVO.class);
        List<TagVO> tagVOs = tags.stream()
                .map(t -> BeanUtil.copyProperties(t, TagVO.class))
                .collect(Collectors.toList());

        ArticleVO vo = BeanUtil.copyProperties(article, ArticleVO.class);
        vo.setCategory(categoryVO);
        vo.setTags(tagVOs);
        vo.setCreatedAt(article.getCreatedAt() != null ? article.getCreatedAt().format(FORMATTER) : null);
        return vo;
    }

    private ArticleDetailVO buildArticleDetailVO(Articles article) {
        Categories category = article.getCategoryId() != null
                ? categoriesMapper.selectOneById(article.getCategoryId()) : null;
        List<Tags> tags = getTagsByArticleId(article.getId());
        User author = userMapper.selectOneById(article.getAuthorId());

        CategoryVO categoryVO = BeanUtil.copyProperties(category, CategoryVO.class);

        AuthorVO authorVO = BeanUtil.copyProperties(author, AuthorVO.class);

        List<TagVO> tagVOs = tags.stream()
                .map(t -> BeanUtil.copyProperties(t, TagVO.class))
                .collect(Collectors.toList());

        ArticleDetailVO vo = BeanUtil.copyProperties(article, ArticleDetailVO.class);
        vo.setCommentCount(getCommentCount(article.getId()));
        vo.setCategory(categoryVO);
        vo.setTags(tagVOs);
        vo.setAuthor(authorVO);
        vo.setCreatedAt(article.getCreatedAt() != null ? article.getCreatedAt().format(FORMATTER) : null);
        vo.setUpdatedAt(article.getUpdatedAt() != null ? article.getUpdatedAt().format(FORMATTER) : null);
        return vo;
    }

    private List<Tags> getTagsByArticleId(String articleId) {
        List<ArticleTag> articleTags = articleTagMapper.selectListByQuery(
                QueryWrapper.create().where(ARTICLE_TAG.ARTICLE_ID.eq(articleId)));
        if (CollUtil.isEmpty(articleTags)) {
            return Collections.emptyList();
        }
        List<String> tagIds = articleTags.stream().map(ArticleTag::getTagId).collect(Collectors.toList());
        return tagsMapper.selectListByIds(tagIds);
    }

    private void batchFillCommentCounts(List<ArticleVO> voList) {
        if (CollUtil.isEmpty(voList)) {
            return;
        }
        List<String> articleIds = voList.stream().map(ArticleVO::getId).collect(Collectors.toList());
        List<Comments> comments = commentsMapper.selectListByQuery(
                QueryWrapper.create()
                        .select(COMMENTS.ARTICLE_ID)
                        .where(COMMENTS.ARTICLE_ID.in(articleIds))
                        .and(COMMENTS.STATUS.eq(1)));
        Map<String, Integer> countMap = MapUtil.newHashMap(articleIds.size());
        for (Comments c : comments) {
            countMap.merge(c.getArticleId(), 1, Integer::sum);
        }
        for (ArticleVO vo : voList) {
            vo.setCommentCount(countMap.getOrDefault(vo.getId(), 0));
        }
    }

    private int getCommentCount(String articleId) {
        return (int) commentsMapper.selectCountByQuery(
                QueryWrapper.create()
                        .where(COMMENTS.ARTICLE_ID.eq(articleId))
                        .and(COMMENTS.STATUS.eq(1)));
    }

    @Override
    public SearchPageVO search(SearchDTO searchDTO) {
        String keyword = searchDTO.getKeyword();
        QueryWrapper qw = QueryWrapper.create()
                .from(ARTICLES)
                .where(ARTICLES.STATUS.eq(1))
                .and(ARTICLES.TITLE.like(StrUtil.format("%{}%", keyword))
                        .or(ARTICLES.CONTENT.like(StrUtil.format("%{}%", keyword))))
                .orderBy(ARTICLES.CREATED_AT, false);

        Page<Articles> page = page(Page.of(searchDTO.getPage(), searchDTO.getPageSize()), qw);

        List<SearchVO> voList = page.getRecords().stream()
                .map(article -> buildSearchVO(article, keyword))
                .collect(Collectors.toList());

        return SearchPageVO.builder()
                .total(page.getTotalRow())
                .page(searchDTO.getPage())
                .pageSize(searchDTO.getPageSize())
                .list(voList)
                .build();
    }

    private SearchVO buildSearchVO(Articles article, String keyword) {
        Categories category = article.getCategoryId() != null
                ? categoriesMapper.selectOneById(article.getCategoryId()) : null;
        List<Tags> tags = getTagsByArticleId(article.getId());

        CategoryVO categoryVO = BeanUtil.copyProperties(category, CategoryVO.class);

        List<TagVO> tagVOs = tags.stream()
                .map(t -> BeanUtil.copyProperties(t, TagVO.class))
                .collect(Collectors.toList());

        String highlight = buildHighlight(article.getTitle(), article.getContent(), keyword);

        SearchVO vo = BeanUtil.copyProperties(article, SearchVO.class);
        vo.setCommentCount(getCommentCount(article.getId()));
        vo.setHighlight(highlight);
        vo.setCategory(categoryVO);
        vo.setTags(tagVOs);
        vo.setCreatedAt(article.getCreatedAt() != null ? article.getCreatedAt().format(FORMATTER) : null);
        return vo;
    }

    private String buildHighlight(String title, String content, String keyword) {
        StringBuilder sb = StrUtil.builder();
        String highlightFormat = "<span style='color:red;'>$0</span>";
        if (StrUtil.contains(title, keyword)) {
            sb.append(title.replaceAll(java.util.regex.Pattern.quote(keyword), highlightFormat.replace("$0", keyword)));
        }
        if (StrUtil.contains(content, keyword)) {
            int idx = content.indexOf(keyword);
            int start = Math.max(0, idx - 50);
            int end = Math.min(content.length(), idx + keyword.length() + 50);
            String snippet = StrUtil.sub(content, start, end);
            if (start > 0) {
                snippet = "..." + snippet;
            }
            if (end < content.length()) {
                snippet = snippet + "...";
            }
            if (sb.length() > 0) {
                sb.append(" | ");
            }
            sb.append(snippet.replaceAll(java.util.regex.Pattern.quote(keyword), highlightFormat.replace("$0", keyword)));
        }
        return sb.toString();
    }

    @Override
    public DashboardVO getDashboardStats() {
        long articleCount = count(QueryWrapper.create().where(ARTICLES.STATUS.eq(1)));
        long commentCount = commentsMapper.selectCountByQuery(
                QueryWrapper.create().where(COMMENTS.STATUS.eq(1)));
        long totalViews = 0L;
        long totalLikes = 0L;
        List<Articles> articles = list(QueryWrapper.create()
                .select(ARTICLES.VIEWS, ARTICLES.LIKES)
                .where(ARTICLES.STATUS.eq(1)));
        for (Articles a : articles) {
            totalViews += ObjectUtil.defaultIfNull(a.getViews(), 0L);
            totalLikes += ObjectUtil.defaultIfNull(a.getLikes(), 0L);
        }

        List<Comments> latestComments = commentsMapper.selectListByQuery(
                QueryWrapper.create()
                        .where(COMMENTS.STATUS.eq(1))
                        .orderBy(COMMENTS.CREATED_AT, false)
                        .limit(5));

        List<CommentVO> commentVOs = latestComments.stream()
                .map(c -> {
                    CommentVO vo = BeanUtil.copyProperties(c, CommentVO.class);
                    vo.setCreatedAt(c.getCreatedAt() != null ? c.getCreatedAt().format(FORMATTER) : null);
                    return vo;
                })
                .collect(Collectors.toList());

        return DashboardVO.builder()
                .articleCount(articleCount)
                .commentCount(commentCount)
                .totalViews(totalViews)
                .totalLikes(totalLikes)
                .latestComments(commentVOs)
                .build();
    }
}