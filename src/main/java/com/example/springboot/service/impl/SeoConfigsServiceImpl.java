package com.example.springboot.service.impl;

import com.example.springboot.common.exception.BusinessException;
import com.example.springboot.common.exception.ErrorCode;
import com.example.springboot.entity.SeoConfigs;
import com.example.springboot.mapper.SeoConfigsMapper;
import com.example.springboot.service.SeoConfigsService;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.example.springboot.entity.table.SeoConfigsTableDef.SEO_CONFIGS;

@Service
public class SeoConfigsServiceImpl extends ServiceImpl<SeoConfigsMapper, SeoConfigs> implements SeoConfigsService {

    private static final Logger log = LoggerFactory.getLogger(SeoConfigsServiceImpl.class);

    @Override
    public SeoConfigs getSeoConfigByPageType(String pageType) {
        SeoConfigs config = getOne(QueryWrapper.create()
                .where(SEO_CONFIGS.PAGE_TYPE.eq(pageType)));
        if (config == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "该页面的SEO配置不存在: " + pageType);
        }
        return config;
    }

    @Override
    @Transactional
    public boolean updateSeoConfig(String pageType, SeoConfigs seoConfigs) {
        SeoConfigs existing = getOne(QueryWrapper.create()
                .where(SEO_CONFIGS.PAGE_TYPE.eq(pageType)));
        if (existing == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "该页面的SEO配置不存在: " + pageType);
        }
        existing.setMetaTitle(seoConfigs.getMetaTitle());
        existing.setMetaKeywords(seoConfigs.getMetaKeywords());
        existing.setMetaDescription(seoConfigs.getMetaDescription());
        existing.setOgTitle(seoConfigs.getOgTitle());
        existing.setOgDescription(seoConfigs.getOgDescription());
        existing.setOgImage(seoConfigs.getOgImage());
        existing.setCanonicalUrl(seoConfigs.getCanonicalUrl());
        existing.setRobots(seoConfigs.getRobots());
        existing.setUpdatedAt(LocalDateTime.now());
        updateById(existing);
        log.info("SEO config updated: pageType={}", pageType);
        return true;
    }
}