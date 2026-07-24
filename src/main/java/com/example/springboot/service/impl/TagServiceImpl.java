package com.example.springboot.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.example.springboot.common.exception.BusinessException;
import com.example.springboot.common.exception.ErrorCode;
import com.example.springboot.common.exception.ThrowUtils;
import com.example.springboot.dto.TagCreateDTO;
import com.example.springboot.dto.TagUpdateDTO;
import com.example.springboot.entity.Tags;
import com.example.springboot.mapper.ArticleTagMapper;
import com.example.springboot.mapper.CategoriesMapper;
import com.example.springboot.mapper.TagsMapper;
import com.example.springboot.service.TagService;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.example.springboot.entity.table.ArticleTagTableDef.ARTICLE_TAG;
import static com.example.springboot.entity.table.TagsTableDef.TAGS;

@Service
public class TagServiceImpl extends ServiceImpl<TagsMapper, Tags> implements TagService {

    private static final Logger log = LoggerFactory.getLogger(TagServiceImpl.class);

    @Resource
    private ArticleTagMapper articleTagMapper;

    @Resource
    private CategoriesMapper categoriesMapper;

    @Override
    public List<Tags> listTags() {
        return list(QueryWrapper.create().orderBy(TAGS.CREATED_AT, true));
    }

    @Override
    @Transactional
    public Tags createTag(TagCreateDTO createDTO) {
        Tags tag = BeanUtil.copyProperties(createDTO, Tags.class);
        tag.setCreatedAt(LocalDateTime.now());
        tag.setUpdatedAt(LocalDateTime.now());

        save(tag);
        log.info("Tag created: id={}, name={}", tag.getId(), tag.getName());
        return tag;
    }

    @Override
    @Transactional
    public Tags updateTag(String id, TagUpdateDTO updateDTO) {
        Tags tag = getById(id);
        if (tag == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "标签不存在");
        }

        BeanUtil.copyProperties(updateDTO, tag, true);
        tag.setUpdatedAt(LocalDateTime.now());
        updateById(tag);

        log.info("Tag updated: id={}, name={}", id, updateDTO.getName());
        return tag;
    }

    @Override
    @Transactional
    public boolean deleteTag(String id) {
        Tags tag = getById(id);
        if (tag == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "标签不存在");
        }

        int byQuery = articleTagMapper.deleteByQuery(QueryWrapper.create().where(ARTICLE_TAG.TAG_ID.eq(id)));
        ThrowUtils.throwIf(byQuery < 0, ErrorCode.OPERATION_ERROR, "标签-文章 删除关联失败");

        boolean removed = removeById(id);
        ThrowUtils.throwIf(!removed, ErrorCode.OPERATION_ERROR, "标签删除失败");
        log.info("Tag deleted: id={}", id);
        return true;
    }

    @Override
    public List<Tags> searchTags(String keyword) {
        return list(QueryWrapper.create()
                .where(TAGS.NAME.like(StrUtil.format("%{}%", keyword)))
                .orderBy(TAGS.CREATED_AT, true));
    }

}