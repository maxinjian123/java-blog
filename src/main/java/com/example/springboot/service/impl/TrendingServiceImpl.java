package com.example.springboot.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.example.springboot.entity.ArticleTag;
import com.example.springboot.entity.Articles;
import com.example.springboot.entity.Categories;
import com.example.springboot.entity.Tags;
import com.example.springboot.mapper.ArticleTagMapper;
import com.example.springboot.mapper.ArticlesMapper;
import com.example.springboot.mapper.CategoriesMapper;
import com.example.springboot.mapper.CommentsMapper;
import com.example.springboot.mapper.TagsMapper;
import com.example.springboot.service.TrendingService;
import com.example.springboot.util.RedisUtil;
import com.example.springboot.vo.CategoryVO;
import com.example.springboot.vo.TagVO;
import com.example.springboot.vo.TrendingVO;
import com.mybatisflex.core.query.QueryWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.example.springboot.entity.table.ArticleTagTableDef.ARTICLE_TAG;
import static com.example.springboot.entity.table.ArticlesTableDef.ARTICLES;
import static com.example.springboot.entity.table.CommentsTableDef.COMMENTS;

@Service
public class TrendingServiceImpl implements TrendingService {

    private static final Logger log = LoggerFactory.getLogger(TrendingServiceImpl.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String KEY_DAILY = "article:trending:daily:";
    private static final String KEY_WEEKLY = "article:trending:weekly:";
    private static final String KEY_MONTHLY = "article:trending:monthly:";
    private static final String KEY_ALL = "article:trending:all";
    private static final int MAX_LIMIT = 50;
    private static final double WEIGHT_VIEW = 1.0;
    private static final double WEIGHT_LIKE = 5.0;
    private static final double WEIGHT_COMMENT = 10.0;
    private static final String PERIOD_WEEKLY = "weekly";
    private static final String PERIOD_MONTHLY = "monthly";
    private static final String PERIOD_ALL = "all";

    private final RedisUtil redisUtil;
    private final ArticlesMapper articlesMapper;
    private final CategoriesMapper categoriesMapper;
    private final TagsMapper tagsMapper;
    private final ArticleTagMapper articleTagMapper;
    private final CommentsMapper commentsMapper;

    public TrendingServiceImpl(RedisUtil redisUtil, ArticlesMapper articlesMapper,
                               CategoriesMapper categoriesMapper, TagsMapper tagsMapper,
                               ArticleTagMapper articleTagMapper, CommentsMapper commentsMapper) {
        this.redisUtil = redisUtil;
        this.articlesMapper = articlesMapper;
        this.categoriesMapper = categoriesMapper;
        this.tagsMapper = tagsMapper;
        this.articleTagMapper = articleTagMapper;
        this.commentsMapper = commentsMapper;
    }

    @Override
    public List<TrendingVO> getTrending(String period, int limit) {
        int maxLimit = Math.min(limit, MAX_LIMIT);
        String redisKey = resolveKey(period);

        Set<ZSetOperations.TypedTuple<Object>> topEntries = redisUtil.zRevRangeWithScores(redisKey, 0, maxLimit - 1);
        if (CollUtil.isEmpty(topEntries)) {
            return Collections.emptyList();
        }

        List<String> articleIds = topEntries.stream()
                .map(t -> t.getValue() != null ? t.getValue().toString() : null)
                .filter(ObjectUtil::isNotNull)
                .collect(Collectors.toList());

        Map<String, Double> scoreMap = topEntries.stream()
                .filter(t -> t.getValue() != null)
                .collect(Collectors.toMap(t -> t.getValue().toString(), t -> ObjectUtil.defaultIfNull(t.getScore(), 0.0)));

        List<Articles> articles = articlesMapper.selectListByIds(articleIds);
        Map<String, Articles> articleMap = articles.stream()
                .collect(Collectors.toMap(Articles::getId, a -> a));

        List<TrendingVO> result = new ArrayList<>();
        int rank = 1;
        for (String id : articleIds) {
            Articles article = articleMap.get(id);
            if (article == null) {
                continue;
            }
            Categories category = article.getCategoryId() != null
                    ? categoriesMapper.selectOneById(article.getCategoryId()) : null;
            List<Tags> tags = getTagsByArticleId(article.getId());

            CategoryVO categoryVO = BeanUtil.copyProperties(category, CategoryVO.class);
            List<TagVO> tagVOs = tags.stream()
                    .map(t -> BeanUtil.copyProperties(t, TagVO.class))
                    .collect(Collectors.toList());

            TrendingVO vo = BeanUtil.copyProperties(article, TrendingVO.class);
            vo.setHotScore(scoreMap.getOrDefault(id, 0.0));
            vo.setRank(rank++);
            vo.setCategory(categoryVO);
            vo.setTags(tagVOs);
            vo.setCreatedAt(article.getCreatedAt() != null ? article.getCreatedAt().format(FORMATTER) : null);
            result.add(vo);
        }

        return result;
    }

    @Override
    public void incrementView(String articleId) {
        LocalDate today = LocalDate.now();
        String[] keys = getCurrentKeys(today);
        for (String key : keys) {
            redisUtil.zIncrementScore(key, articleId, WEIGHT_VIEW);
        }
    }

    @Override
    public void incrementLike(String articleId) {
        LocalDate today = LocalDate.now();
        String[] keys = getCurrentKeys(today);
        for (String key : keys) {
            redisUtil.zIncrementScore(key, articleId, WEIGHT_LIKE);
        }
    }

    @Override
    public void incrementComment(String articleId) {
        LocalDate today = LocalDate.now();
        String[] keys = getCurrentKeys(today);
        for (String key : keys) {
            redisUtil.zIncrementScore(key, articleId, WEIGHT_COMMENT);
        }
    }

    @Override
    public void rebuildTrending() {
        List<Articles> allArticles = articlesMapper.selectListByQuery(
                QueryWrapper.create().where(ARTICLES.STATUS.eq(1)));
        if (CollUtil.isEmpty(allArticles)) {
            return;
        }
        List<String> allIds = allArticles.stream().map(Articles::getId).collect(Collectors.toList());
        Map<String, Long> commentCountMap = getCommentCountMap(allIds);

        for (Articles article : allArticles) {
            double decayed = computeScoreWithDecay(article, commentCountMap);
            setAllPeriods(article.getId(), decayed);
        }
        log.info("Trending rebuilt: {} articles processed", allArticles.size());
    }

    @Override
    public void removeFromTrending(String articleId) {
        redisUtil.zRemove(KEY_ALL, articleId);
        Set<String> allKeys = Collections.emptySet();
        if (redisUtil.hGetAll("article:trending:keys:set") != null) {
            for (Object key : allKeys) {
                redisUtil.zRemove(key.toString(), articleId);
            }
        }
    }

    private String resolveKey(String period) {
        LocalDate now = LocalDate.now();
        if (StrUtil.equals(PERIOD_WEEKLY, period)) {
            int weekOfYear = (int) Math.ceil(now.getDayOfYear() / 7.0);
            String key = KEY_WEEKLY + now.getYear() + StrUtil.padPre(String.valueOf(weekOfYear), 2, '0');
            redisUtil.expire(key, 14, TimeUnit.DAYS);
            return key;
        }
        if (StrUtil.equals(PERIOD_MONTHLY, period)) {
            String key = KEY_MONTHLY + now.format(DateTimeFormatter.ofPattern("yyyyMM"));
            redisUtil.expire(key, 60, TimeUnit.DAYS);
            return key;
        }
        if (StrUtil.equals(PERIOD_ALL, period)) {
            return KEY_ALL;
        }
        String key = KEY_DAILY + now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        redisUtil.expire(key, 48, TimeUnit.HOURS);
        return key;
    }

    private String[] getCurrentKeys(LocalDate today) {
        String daily = KEY_DAILY + today.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        redisUtil.expire(daily, 48, TimeUnit.HOURS);
        int week = (int) Math.ceil(today.getDayOfYear() / 7.0);
        String weekly = KEY_WEEKLY + today.getYear() + StrUtil.padPre(String.valueOf(week), 2, '0');
        redisUtil.expire(weekly, 14, TimeUnit.DAYS);
        String monthly = KEY_MONTHLY + today.format(DateTimeFormatter.ofPattern("yyyyMM"));
        redisUtil.expire(monthly, 60, TimeUnit.DAYS);
        return new String[]{daily, weekly, monthly, KEY_ALL};
    }

    private void setAllPeriods(String articleId, double score) {
        LocalDate today = LocalDate.now();
        String[] keys = getCurrentKeys(today);
        for (String key : keys) {
            redisUtil.zAdd(key, articleId, score);
        }
    }

    private Map<String, Long> getCommentCountMap(List<String> articleIds) {
        Map<String, Long> result = MapUtil.newHashMap();
        if (CollUtil.isEmpty(articleIds)) {
            return result;
        }
        for (String articleId : articleIds) {
            long count = commentsMapper.selectCountByQuery(
                    QueryWrapper.create()
                            .where(COMMENTS.ARTICLE_ID.eq(articleId))
                            .and(COMMENTS.DELETED.eq(0)));
            if (count > 0) {
                result.put(articleId, count);
            }
        }
        return result;
    }

    private double computeScoreWithDecay(Articles article, Map<String, Long> commentCountMap) {
        long view = ObjectUtil.defaultIfNull(article.getViews(), 0L);
        long like = ObjectUtil.defaultIfNull(article.getLikes(), 0L);
        long comment = commentCountMap.getOrDefault(article.getId(), 0L);
        double raw = view * WEIGHT_VIEW + like * WEIGHT_LIKE + comment * WEIGHT_COMMENT;
        long days = ChronoUnit.DAYS.between(article.getCreatedAt() != null
                ? article.getCreatedAt().toLocalDate() : LocalDate.now(), LocalDate.now());
        double decay = Math.pow(0.95, days);
        return Math.max(0.0, raw * decay);
    }

    private List<Tags> getTagsByArticleId(String articleId) {
        List<ArticleTag> relations = articleTagMapper.selectListByQuery(
                QueryWrapper.create().where(ARTICLE_TAG.ARTICLE_ID.eq(articleId)));
        if (CollUtil.isEmpty(relations)) {
            return Collections.emptyList();
        }
        List<String> tagIds = relations.stream().map(ArticleTag::getTagId).collect(Collectors.toList());
        return tagsMapper.selectListByIds(tagIds);
    }
}