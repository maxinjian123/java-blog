package com.example.springboot.service;

import com.example.springboot.entity.SeoConfigs;
import com.mybatisflex.core.service.IService;

/**
 * SEO 全局与页面配置表 服务层。
 *
 * @author <a href="https://github.com/maxinjian123">潇潇</a>
 */
public interface SeoConfigsService extends IService<SeoConfigs> {

    SeoConfigs getSeoConfigByPageType(String pageType);

    boolean updateSeoConfig(String pageType, SeoConfigs seoConfigs);
}