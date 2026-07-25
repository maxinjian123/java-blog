-- --------------------------------------------------
-- 数据库初始化设置
-- --------------------------------------------------
CREATE DATABASE IF NOT EXISTS `personal_blog`
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE `personal_blog`;

SET FOREIGN_KEY_CHECKS = 0;

-- --------------------------------------------------
-- 1. 用户/管理员表 (users)
-- --------------------------------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`
(
    `id`         VARCHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL COMMENT '用户ID (字符串/UUID/雪花ID)',
    `username`   VARCHAR(50)                                       NOT NULL COMMENT '登录用户名',
    `password`   VARCHAR(255)                                      NOT NULL COMMENT '哈希密码',
    `nickname`   VARCHAR(50)                                       NOT NULL COMMENT '显示昵称',
    `email`      VARCHAR(100)                                               DEFAULT NULL COMMENT '邮箱',
    `avatar`     VARCHAR(255)                                               DEFAULT NULL COMMENT '头像URL',
    `bio`        VARCHAR(255)                                               DEFAULT NULL COMMENT '个人简介',
    `role`       VARCHAR(20)                                       NOT NULL DEFAULT 'admin' COMMENT '角色: admin, author',
    `created_at` DATETIME                                          NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME                                          NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`    INT                                          DEFAULT NULL COMMENT '逻辑删除标识',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_email` (`email`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='用户/管理员表';

-- --------------------------------------------------
-- 2. 文章分类表 (categories)
-- --------------------------------------------------
DROP TABLE IF EXISTS `categories`;
CREATE TABLE `categories`
(
    `id`            VARCHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL COMMENT '分类ID (字符串)',
    `name`          VARCHAR(50)                                       NOT NULL COMMENT '分类名称',
    `slug`          VARCHAR(50)                                       NOT NULL COMMENT '分类别名/URL拼音',
    `description`   VARCHAR(255)                                               DEFAULT NULL COMMENT '分类描述',
    `article_count` INT UNSIGNED                                      NOT NULL DEFAULT 0 COMMENT '冗余字段：文章数量',
    `created_at`    DATETIME                                          NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`    DATETIME                                          NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`       INT                                          DEFAULT NULL COMMENT '逻辑删除标识',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_name` (`name`),
    UNIQUE KEY `uk_slug` (`slug`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='文章分类表';

-- --------------------------------------------------
-- 3. 文章标签表 (tags)
-- --------------------------------------------------
DROP TABLE IF EXISTS `tags`;
CREATE TABLE `tags`
(
    `id`         VARCHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL COMMENT '标签ID (字符串)',
    `name`       VARCHAR(50)                                       NOT NULL COMMENT '标签名称',
    `slug`       VARCHAR(50)                                       NOT NULL COMMENT '标签别名/URL拼音',
    `created_at` DATETIME                                          NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME                                          NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`    INT                                          DEFAULT NULL COMMENT '逻辑删除标识',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_name` (`name`),
    UNIQUE KEY `uk_slug` (`slug`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='文章标签表';

-- --------------------------------------------------
-- 4. 文章主表 (articles)
-- --------------------------------------------------
DROP TABLE IF EXISTS `articles`;
CREATE TABLE `articles`
(
    `id`            VARCHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL COMMENT '文章ID (字符串)',
    `author_id`     VARCHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL COMMENT '作者ID (字符串)',
    `category_id`   VARCHAR(36) CHARACTER SET ascii COLLATE ascii_bin          DEFAULT NULL COMMENT '所属分类ID (字符串)',
    `title`         VARCHAR(200)                                      NOT NULL COMMENT '文章标题',
    `slug`          VARCHAR(200)                                      NOT NULL COMMENT '自定义URL别名',
    `summary`       VARCHAR(500)                                               DEFAULT NULL COMMENT '文章摘要/简介',
    `content`       LONGTEXT                                          NOT NULL COMMENT 'Markdown正文内容',
    `cover`         VARCHAR(255)                                               DEFAULT NULL COMMENT '封面图URL',
    `status`        TINYINT                                           NOT NULL DEFAULT 1 COMMENT '状态: 0-草稿, 1-已发布, 2-隐藏',
    `views`         INT UNSIGNED                                      NOT NULL DEFAULT 0 COMMENT '浏览量 (Redis 异步同步)',
    `likes`         INT UNSIGNED                                      NOT NULL DEFAULT 0 COMMENT '点赞数',
    `comment_count` INT UNSIGNED                                      NOT NULL DEFAULT 0 COMMENT '评论总数',
    `created_at`    DATETIME                                          NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
    `updated_at`    DATETIME                                          NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `deleted`       INT                                          DEFAULT NULL COMMENT '逻辑删除标识',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_slug` (`slug`),
    -- 【深分页与快速列表过滤索引】
    KEY `idx_status_created_id` (`status`, `created_at` DESC, `id`),
    -- 【分类筛选复合索引】
    KEY `idx_category_status` (`category_id`, `status`, `created_at` DESC),
    -- 【作者筛选索引】
    KEY `idx_author_id` (`author_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='文章主表';

-- --------------------------------------------------
-- 5. 文章与标签关联表 (article_tag)
-- --------------------------------------------------
DROP TABLE IF EXISTS `article_tag`;
CREATE TABLE `article_tag`
(
    `article_id` VARCHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL COMMENT '文章ID (字符串)',
    `tag_id`     VARCHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL COMMENT '标签ID (字符串)',
    PRIMARY KEY (`article_id`, `tag_id`),
    -- 【反查聚簇索引】
    KEY `idx_tag_article` (`tag_id`, `article_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='文章与标签关联表';

-- --------------------------------------------------
-- 6. 评论表 (comments)
-- --------------------------------------------------
DROP TABLE IF EXISTS `comments`;
CREATE TABLE `comments`
(
    `id`         VARCHAR(36) CHARACTER SET ascii COLLATE ascii_bin  NOT NULL COMMENT '评论ID (字符串)',
    `article_id` VARCHAR(36) CHARACTER SET ascii COLLATE ascii_bin  NOT NULL COMMENT '关联的文章ID (字符串)',
    `parent_id`  VARCHAR(36) CHARACTER SET ascii COLLATE ascii_bin           DEFAULT NULL COMMENT '父评论ID (字符串，NULL表示顶级)',
    `path`       VARCHAR(500) CHARACTER SET ascii COLLATE ascii_bin NOT NULL DEFAULT '/' COMMENT '物化路径 (例如 /uuid-1/uuid-2/)',
    `nickname`   VARCHAR(50)                                        NOT NULL COMMENT '评论者昵称',
    `email`      VARCHAR(100)                                       NOT NULL COMMENT '评论者邮箱',
    `email_md5`  VARCHAR(32) GENERATED ALWAYS AS (MD5(LOWER(TRIM(`email`)))) STORED COMMENT 'Gravatar 头像 MD5 虚拟生成列',
    # MySQL 版本（9.6 及以上版本）选⬇️
    #  `email_md5`  VARCHAR(32) GENERATED ALWAYS AS (SHA2(email, 256)) VIRTUAL  COMMENT 'Gravatar 头像 MD5 虚拟生成列',
    `website`    VARCHAR(255)                                                DEFAULT NULL COMMENT '个人网站',
    `content`    TEXT                                               NOT NULL COMMENT '评论内容',
    `ip`         VARCHAR(45)                                                 DEFAULT NULL COMMENT '评论者IP',
    `user_agent` VARCHAR(255)                                                DEFAULT NULL COMMENT '设备信息',
    `is_admin`   TINYINT(1)                                         NOT NULL DEFAULT 0 COMMENT '是否为博主回复: 0-否, 1-是',
    `status`     TINYINT                                            NOT NULL DEFAULT 1 COMMENT '状态: 0-待审核, 1-已通过, 2-垃圾评论',
    `created_at` DATETIME                                           NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '评论时间',
    `deleted`    INT                                          DEFAULT NULL COMMENT '逻辑删除标识',
    PRIMARY KEY (`id`),
    -- 【评论列表快速拉取索引】
    KEY `idx_article_parent_status` (`article_id`, `parent_id`, `status`, `created_at` ASC),
    -- 【物化路径快速扫描索引】
    KEY `idx_path` (`path`(100))
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='文章评论表';

-- --------------------------------------------------
-- 7. 系统配置表 (sys_config)
-- --------------------------------------------------
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config`
(
    `id`           VARCHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL COMMENT '配置ID (字符串)',
    `config_key`   VARCHAR(50)                                       NOT NULL COMMENT '配置键名',
    `config_value` TEXT                                                       DEFAULT NULL COMMENT '配置值',
    `remark`       VARCHAR(100)                                               DEFAULT NULL COMMENT '说明',
    `updated_at`   DATETIME                                          NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_config_key` (`config_key`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='系统配置表';

-- --------------------------------------------------
-- 8. 每日访问统计汇总表 (daily_analytics)
-- --------------------------------------------------
DROP TABLE IF EXISTS `daily_analytics`;
CREATE TABLE `daily_analytics`
(
    `date`       DATE         NOT NULL COMMENT '统计日期 (如 2026-03-31)',
    `pv`         INT UNSIGNED NOT NULL DEFAULT 0 COMMENT 'PV访问量',
    `uv`         INT UNSIGNED NOT NULL DEFAULT 0 COMMENT 'UV独立访客',
    `created_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`date`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='每日访问统计表';

-- --------------------------------------------------
-- 1. SEO 配置表 (seo_configs)
-- 用于管理首页、分类页、自定义页面的全局 Meta/OG 标签
-- --------------------------------------------------
DROP TABLE IF EXISTS `seo_configs`;
CREATE TABLE `seo_configs`
(
    `id`               VARCHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL COMMENT 'SEO配置ID (字符串/UUID)',
    `page_type`        VARCHAR(50)                                       NOT NULL COMMENT '页面类型: home(首页), archive(归档), about(关于页) 等',
    `meta_title`       VARCHAR(200)                                      NOT NULL COMMENT 'SEO 标题 (Title)',
    `meta_keywords`    VARCHAR(255)                                               DEFAULT NULL COMMENT 'SEO 关键词 (Keywords)',
    `meta_description` VARCHAR(500)                                               DEFAULT NULL COMMENT 'SEO 描述 (Description)',
    `og_title`         VARCHAR(200)                                               DEFAULT NULL COMMENT 'Open Graph 社交分享标题',
    `og_description`   VARCHAR(500)                                               DEFAULT NULL COMMENT 'Open Graph 社交分享描述',
    `og_image`         VARCHAR(255)                                               DEFAULT NULL COMMENT 'Open Graph 分享卡片封面图 URL',
    `canonical_url`    VARCHAR(255)                                               DEFAULT NULL COMMENT '规范网页链接',
    `robots`           VARCHAR(50)                                       NOT NULL DEFAULT 'index, follow' COMMENT '搜索引擎爬虫指令',
    `updated_at`       DATETIME                                          NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_page_type` (`page_type`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='SEO 全局与页面配置表';

ALTER TABLE `articles`
    ADD COLUMN `seo_title`       VARCHAR(200) DEFAULT NULL COMMENT '独立 SEO 标题 (若为空则用文章 title)' AFTER `cover`,
    ADD COLUMN `seo_keywords`    VARCHAR(255) DEFAULT NULL COMMENT '独立 SEO 关键词' AFTER `seo_title`,
    ADD COLUMN `seo_description` VARCHAR(500) DEFAULT NULL COMMENT '独立 SEO 描述 (若为空则用 summary)' AFTER `seo_keywords`;

SET FOREIGN_KEY_CHECKS = 1;