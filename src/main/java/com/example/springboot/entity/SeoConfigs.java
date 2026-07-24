package com.example.springboot.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.keygen.KeyGenerators;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SEO 全局与页面配置表 实体类。
 *
 * @author <a href="https://github.com/maxinjian123">潇潇</a>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("seo_configs")
public class SeoConfigs implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * SEO配置ID (字符串/UUID)
     */
    @Id(keyType = KeyType.Generator, value = KeyGenerators.snowFlakeId)
    private String id;

    /**
     * 页面类型: home(首页), archive(归档), about(关于页) 等
     */
    private String pageType;

    /**
     * SEO 标题 (Title)
     */
    private String metaTitle;

    /**
     * SEO 关键词 (Keywords)
     */
    private String metaKeywords;

    /**
     * SEO 描述 (Description)
     */
    private String metaDescription;

    /**
     * Open Graph 社交分享标题
     */
    private String ogTitle;

    /**
     * Open Graph 社交分享描述
     */
    private String ogDescription;

    /**
     * Open Graph 分享卡片封面图 URL
     */
    private String ogImage;

    /**
     * 规范网页链接
     */
    private String canonicalUrl;

    /**
     * 搜索引擎爬虫指令
     */
    private String robots;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

}