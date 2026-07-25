SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS users;
CREATE TABLE users
(
    id         VARCHAR(36) NOT NULL,
    username   VARCHAR(50) NOT NULL,
    password   VARCHAR(255) NOT NULL,
    nickname   VARCHAR(50) NOT NULL,
    email      VARCHAR(100) DEFAULT NULL,
    avatar     VARCHAR(255) DEFAULT NULL,
    bio        VARCHAR(255) DEFAULT NULL,
    role       VARCHAR(20) NOT NULL DEFAULT 'admin',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted    INT DEFAULT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_users_username UNIQUE (username),
    CONSTRAINT uk_users_email UNIQUE (email)
);

DROP TABLE IF EXISTS categories;
CREATE TABLE categories
(
    id            VARCHAR(36) NOT NULL,
    name          VARCHAR(50) NOT NULL,
    slug          VARCHAR(50) NOT NULL,
    description   VARCHAR(255) DEFAULT NULL,
    article_count INT NOT NULL DEFAULT 0,
    created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted       INT DEFAULT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_categories_name UNIQUE (name),
    CONSTRAINT uk_categories_slug UNIQUE (slug)
);

DROP TABLE IF EXISTS tags;
CREATE TABLE tags
(
    id         VARCHAR(36) NOT NULL,
    name       VARCHAR(50) NOT NULL,
    slug       VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted    INT DEFAULT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_tags_name UNIQUE (name),
    CONSTRAINT uk_tags_slug UNIQUE (slug)
);

DROP TABLE IF EXISTS articles;
CREATE TABLE articles
(
    id              VARCHAR(36) NOT NULL,
    author_id       VARCHAR(36) NOT NULL,
    category_id     VARCHAR(36) DEFAULT NULL,
    title           VARCHAR(200) NOT NULL,
    slug            VARCHAR(200) NOT NULL,
    summary         VARCHAR(500) DEFAULT NULL,
    content         CLOB NOT NULL,
    cover           VARCHAR(255) DEFAULT NULL,
    seo_title       VARCHAR(200) DEFAULT NULL,
    seo_keywords    VARCHAR(255) DEFAULT NULL,
    seo_description VARCHAR(500) DEFAULT NULL,
    status          TINYINT NOT NULL DEFAULT 1,
    views           INT NOT NULL DEFAULT 0,
    likes           INT NOT NULL DEFAULT 0,
    comment_count   INT NOT NULL DEFAULT 0,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted         INT DEFAULT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_articles_slug UNIQUE (slug)
);

DROP TABLE IF EXISTS article_tag;
CREATE TABLE article_tag
(
    article_id VARCHAR(36) NOT NULL,
    tag_id     VARCHAR(36) NOT NULL,
    PRIMARY KEY (article_id, tag_id)
);

DROP TABLE IF EXISTS comments;
CREATE TABLE comments
(
    id         VARCHAR(36) NOT NULL,
    article_id VARCHAR(36) NOT NULL,
    parent_id  VARCHAR(36) DEFAULT NULL,
    path       VARCHAR(500) NOT NULL DEFAULT '/',
    nickname   VARCHAR(50) NOT NULL,
    email      VARCHAR(100) NOT NULL,
    email_md5  VARCHAR(32) DEFAULT NULL,
    website    VARCHAR(255) DEFAULT NULL,
    content    CLOB NOT NULL,
    ip         VARCHAR(45) DEFAULT NULL,
    user_agent VARCHAR(255) DEFAULT NULL,
    is_admin   TINYINT(1) NOT NULL DEFAULT 0,
    status     TINYINT NOT NULL DEFAULT 1,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted    INT DEFAULT NULL,
    PRIMARY KEY (id)
);

DROP TABLE IF EXISTS sys_config;
CREATE TABLE sys_config
(
    id           VARCHAR(36) NOT NULL,
    config_key   VARCHAR(50) NOT NULL,
    config_value CLOB DEFAULT NULL,
    remark       VARCHAR(100) DEFAULT NULL,
    updated_at   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT uk_sys_config_key UNIQUE (config_key)
);

DROP TABLE IF EXISTS daily_analytics;
CREATE TABLE daily_analytics
(
    date       DATE NOT NULL,
    pv         INT NOT NULL DEFAULT 0,
    uv         INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (date)
);

DROP TABLE IF EXISTS seo_configs;
CREATE TABLE seo_configs
(
    id               VARCHAR(36) NOT NULL,
    page_type        VARCHAR(50) NOT NULL,
    meta_title       VARCHAR(200) NOT NULL,
    meta_keywords    VARCHAR(255) DEFAULT NULL,
    meta_description VARCHAR(500) DEFAULT NULL,
    og_title         VARCHAR(200) DEFAULT NULL,
    og_description   VARCHAR(500) DEFAULT NULL,
    og_image         VARCHAR(255) DEFAULT NULL,
    canonical_url    VARCHAR(255) DEFAULT NULL,
    robots           VARCHAR(50) NOT NULL DEFAULT 'index, follow',
    updated_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT uk_seo_configs_page_type UNIQUE (page_type)
);

SET FOREIGN_KEY_CHECKS = 1;