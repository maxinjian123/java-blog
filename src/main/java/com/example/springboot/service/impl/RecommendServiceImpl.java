package com.example.springboot.service.impl;

import com.example.springboot.common.exception.BusinessException;
import com.example.springboot.common.exception.ErrorCode;
import com.example.springboot.entity.ArticleTag;
import com.example.springboot.entity.Articles;
import com.example.springboot.entity.Categories;
import com.example.springboot.entity.Tags;
import com.example.springboot.mapper.ArticleTagMapper;
import com.example.springboot.mapper.ArticlesMapper;
import com.example.springboot.mapper.CategoriesMapper;
import com.example.springboot.mapper.TagsMapper;
import com.example.springboot.service.RecommendService;
import com.example.springboot.util.RedisUtil;
import com.example.springboot.vo.RecommendVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.example.springboot.entity.table.ArticleTagTableDef.ARTICLE_TAG;
import static com.example.springboot.entity.table.ArticlesTableDef.ARTICLES;

import com.mybatisflex.core.query.QueryWrapper;

@Service
public class RecommendServiceImpl implements RecommendService {

    private static final Logger log = LoggerFactory.getLogger(RecommendServiceImpl.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String KEY_RECOMMEND = "article:recommend:";
    private static final int TTL_DAYS = 7;
    private static final int TOP_N = 10;
    private static final double CATEGORY_WEIGHT = 0.4;
    private static final double TAG_WEIGHT = 0.6;

    private final RedisUtil redisUtil;
    private final ArticlesMapper articlesMapper;
    private final ArticleTagMapper articleTagMapper;
    private final TagsMapper tagsMapper;
    private final CategoriesMapper categoriesMapper;

    public RecommendServiceImpl(RedisUtil redisUtil, ArticlesMapper articlesMapper,
                                ArticleTagMapper articleTagMapper, TagsMapper tagsMapper,
                                CategoriesMapper categoriesMapper) {
        this.redisUtil = redisUtil;
        this.articlesMapper = articlesMapper;
        this.articleTagMapper = articleTagMapper;
        this.tagsMapper = tagsMapper;
        this.categoriesMapper = categoriesMapper;
    }

    @Override
    public List<RecommendVO> getRecommend(String articleId, int limit) {
        Articles article = articlesMapper.selectOneById(articleId);
        if (article == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "文章不存在");
        }

        String cacheKey = KEY_RECOMMEND + articleId;
        Object cached = redisUtil.get(cacheKey);
        @SuppressWarnings("unchecked")
        List<String> cachedIds = cached instanceof List ? (List<String>) cached : null;

        if (cachedIds == null || cachedIds.isEmpty()) {
            refreshRecommend(articleId);
            cached = redisUtil.get(cacheKey);
            cachedIds = cached instanceof List ? (List<String>) cached : Collections.emptyList();
        }

        int actualLimit = Math.min(limit, cachedIds.size());
        List<String> topIds = cachedIds.subList(0, actualLimit);
        return fetchRecommendResults(article, topIds);
    }

    @Override
    public void refreshRecommend(String articleId) {
        Articles source = articlesMapper.selectOneById(articleId);
        if (source == null) {
            return;
        }

        List<Articles> candidates = articlesMapper.selectListByQuery(
                QueryWrapper.create()
                        .where(ARTICLES.STATUS.eq(1))
                        .and(ARTICLES.ID.ne(articleId)));

        if (candidates.isEmpty()) {
            return;
        }

        Set<String> sourceTags = getTagIdsByArticle(articleId);
        String sourceCategory = source.getCategoryId();
        Set<String> sourceKeywords = extractKeywords(source);

        List<Map.Entry<String, Double>> scored = new ArrayList<>();
        for (Articles candidate : candidates) {
            Set<String> candTags = getTagIdsByArticle(candidate.getId());
            String candCategory = candidate.getCategoryId();
            Set<String> candKeywords = extractKeywords(candidate);

            double catSim = sourceCategory != null && sourceCategory.equals(candCategory) ? 1.0 : 0.0;
            double tagSim = jaccard(sourceTags, candTags);
            double keywordSim = jaccard(sourceKeywords, candKeywords);

            double similarity = catSim * CATEGORY_WEIGHT
                    + tagSim * TAG_WEIGHT * 0.7
                    + keywordSim * TAG_WEIGHT * 0.3;

            if (similarity > 0.0) {
                scored.add(new AbstractMap.SimpleEntry<>(candidate.getId(), similarity));
            }
        }

        scored.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));
        List<String> topIds = scored.stream()
                .limit(TOP_N)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        redisUtil.set(KEY_RECOMMEND + articleId, topIds, TTL_DAYS, TimeUnit.DAYS);
        log.info("Recommend refreshed: articleId={}, candidates={}", articleId, topIds.size());
    }

    @Override
    public void removeRecommend(String articleId) {
        redisUtil.delete(KEY_RECOMMEND + articleId);
    }

    private Set<String> getTagIdsByArticle(String articleId) {
        List<ArticleTag> relations = articleTagMapper.selectListByQuery(
                QueryWrapper.create().where(ARTICLE_TAG.ARTICLE_ID.eq(articleId)));
        return relations.stream()
                .map(ArticleTag::getTagId)
                .collect(Collectors.toSet());
    }

    private Set<String> extractKeywords(Articles article) {
        Set<String> keywords = new HashSet<>();
        if (article.getTitle() != null) {
            String[] tokens = article.getTitle().split("[\\s,，.。:：;；!?！？()（）\\-—\\[\\]【】]+");
            for (String t : tokens) {
                if (t.length() >= 2) {
                    keywords.add(t);
                }
            }
        }
        if (article.getSummary() != null) {
            String[] tokens = article.getSummary().split("[\\s,，.。:：;；!?！？()（）\\-—\\[\\]【】]+");
            for (String t : tokens) {
                if (t.length() >= 2) {
                    keywords.add(t);
                }
            }
        }
        return keywords;
    }

    private double jaccard(Set<String> a, Set<String> b) {
        if (a.isEmpty() || b.isEmpty()) {
            return 0.0;
        }
        Set<String> intersection = new HashSet<>(a);
        intersection.retainAll(b);
        if (intersection.isEmpty()) {
            return 0.0;
        }
        Set<String> union = new HashSet<>(a);
        union.addAll(b);
        return (double) intersection.size() / union.size();
    }

    private List<RecommendVO> fetchRecommendResults(Articles source, List<String> ids) {
        if (ids.isEmpty()) {
            return Collections.emptyList();
        }
        List<Articles> articles = articlesMapper.selectListByIds(ids);
        Map<String, Articles> articleMap = articles.stream()
                .collect(Collectors.toMap(Articles::getId, a -> a));

        Map<String, Tags> tagNameMap = new HashMap<>();
        for (Tags t : tagsMapper.selectAll()) {
            tagNameMap.put(t.getId(), t);
        }
        Map<String, Categories> categoryMap = new HashMap<>();
        for (Categories c : categoriesMapper.selectAll()) {
            categoryMap.put(c.getId(), c);
        }

        Set<String> sourceTags = getTagIdsByArticle(source.getId());
        List<RecommendVO> result = new ArrayList<>();
        for (String id : ids) {
            Articles a = articleMap.get(id);
            if (a == null) {
                continue;
            }
            Set<String> candTags = getTagIdsByArticle(id);
            candTags.retainAll(sourceTags);
            List<String> commonTagNames = candTags.stream()
                    .map(tid -> tagNameMap.get(tid))
                    .filter(t -> t != null)
                    .map(Tags::getName)
                    .limit(3)
                    .collect(Collectors.toList());

            String reason;
            if (source.getCategoryId() != null && source.getCategoryId().equals(a.getCategoryId())) {
                Categories srcCat = categoryMap.get(source.getCategoryId());
                if (!commonTagNames.isEmpty()) {
                    reason = "同分类「" + (srcCat != null ? srcCat.getName() : "") + "」，包含相似标签: " + String.join(", ", commonTagNames);
                } else {
                    reason = "同分类「" + (srcCat != null ? srcCat.getName() : "") + "」的相关文章";
                }
            } else if (!commonTagNames.isEmpty()) {
                reason = "包含相似标签: " + String.join(", ", commonTagNames);
            } else {
                reason = "本文提及的高频词与您阅读的文章相似";
            }

            Set<String> sourceKw = extractKeywords(source);
            Set<String> candKw = extractKeywords(a);
            double sim = jaccard(sourceTags, getTagIdsByArticle(id)) * 0.7
                    + jaccard(sourceKw, candKw) * 0.3;

            result.add(RecommendVO.builder()
                    .id(a.getId())
                    .title(a.getTitle())
                    .slug(a.getSlug())
                    .summary(a.getSummary())
                    .cover(a.getCover())
                    .views(a.getViews())
                    .likes(a.getLikes())
                    .similarity(Math.round(sim * 100.0) / 100.0)
                    .recommendReason(reason)
                    .createdAt(a.getCreatedAt() != null ? a.getCreatedAt().format(FORMATTER) : null)
                    .build());
        }
        return result;
    }
}