DROP TABLE IF EXISTS article;
CREATE TABLE article
(
    id           INT          NOT NULL AUTO_INCREMENT COMMENT '自增id',
    title        VARCHAR(255) NOT NULL COMMENT '标题',
    content      TEXT         NOT NULL COMMENT '内容',
    cover        TEXT COMMENT '头图路径',
    video        TEXT COMMENT '视频路径',
    source       VARCHAR(255) COMMENT '文章来源',
    type         INT COMMENT '文章类型，0-text，1-media，2-video',
    class_id     INT COMMENT '分类ID',
    tag_id       VARCHAR(255) COMMENT '标签ID组，ID使用英文逗号分隔',
    viewed       INT          NOT NULL DEFAULT 0 COMMENT '浏览量',
    like_num     INT          NOT NULL DEFAULT 0 COMMENT '点赞数量',
    favorite_num INT          NOT NULL DEFAULT 0 COMMENT '收藏数量',
    short_text   VARCHAR(255) COMMENT '简介',
    created_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    created_by   INT          NOT NULL COMMENT '创建人',
    status       INT          NOT NULL DEFAULT 1 COMMENT '文章状态, 0-Deleted, 1-Normal',
    PRIMARY KEY (id),
    CONSTRAINT title UNIQUE (title)
) COMMENT = '文章';


DROP TABLE IF EXISTS category;
CREATE TABLE category
(
    id         INT          NOT NULL AUTO_INCREMENT COMMENT 'id',
    title      VARCHAR(255) NOT NULL COMMENT '标题',
    bg_color   CHAR(10) COMMENT '背景色',
    text_color CHAR(10) COMMENT '文字颜色',
    icon       TEXT COMMENT '栏目图标',
    banner     VARCHAR(255) COMMENT 'banner（文章ID列表，ID使用英文逗号分隔）',
    tag_id     VARCHAR(255) COMMENT '标签ID组，ID使用英文逗号分隔',
    class_id   VARCHAR(255) COMMENT '分类',
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    status     INT          NOT NULL DEFAULT 1 COMMENT '状态, 0-Deleted, 1-Normal',
    sort       INT          NOT NULL DEFAULT 0 COMMENT '手动排序，DESC',
    PRIMARY KEY (id),
    CONSTRAINT title UNIQUE (title)
) COMMENT = '栏目';

DROP TABLE IF EXISTS topic;
CREATE TABLE topic
(
    id          INT          NOT NULL AUTO_INCREMENT COMMENT 'id',
    category_id INT          NOT NULL COMMENT '栏目',
    title       VARCHAR(255) NOT NULL COMMENT '标题',
    icon        TEXT COMMENT '栏目图标',
    cover       TEXT COMMENT '栏目封面',
    tag_id      VARCHAR(255) COMMENT '标签ID组，ID使用英文逗号分隔',
    class_id    VARCHAR(255) COMMENT '分类',
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    sort        INT          NOT NULL DEFAULT 0 COMMENT '手动排序，DESC',
    status      INT          NOT NULL DEFAULT 1 COMMENT '状态, 0-Deleted, 1-Normal',
    PRIMARY KEY (id)
) COMMENT = '专题';

DROP TABLE IF EXISTS classification;
CREATE TABLE classification
(
    id         INT          NOT NULL AUTO_INCREMENT COMMENT 'id',
    title      VARCHAR(255) NOT NULL COMMENT '标题',
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    status     INT          NOT NULL DEFAULT 1 COMMENT '状态, 0-Deleted, 1-Normal',
    PRIMARY KEY (id),
    CONSTRAINT title UNIQUE (title)
) COMMENT = '分类';

DROP TABLE IF EXISTS tag;
CREATE TABLE tag
(
    id         INT          NOT NULL AUTO_INCREMENT COMMENT 'id',
    title      VARCHAR(255) NOT NULL COMMENT '标题',
    bg_color   CHAR(10) COMMENT '背景色',
    text_color CHAR(10) COMMENT '文字颜色',
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    status     INT          NOT NULL DEFAULT 1 COMMENT '状态, 0-Deleted, 1-Normal',
    PRIMARY KEY (id),
    CONSTRAINT title UNIQUE (title)
) COMMENT = '标签';

DROP TABLE IF EXISTS sort_rel;
CREATE TABLE sort_rel
(
    id           INT      NOT NULL AUTO_INCREMENT COMMENT 'id',
    article_id   INT      NOT NULL COMMENT '文章',
    category_id  INT COMMENT '栏目',
    topic_id     INT COMMENT '专题',
    is_sticky    INT      NOT NULL DEFAULT 0 COMMENT '是否置顶， 0-不置顶，1-置顶',
    sort         INT      NOT NULL COMMENT '手动排序，ASC',
    created_at   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    status       INT      NOT NULL DEFAULT 1 COMMENT '状态, 0-Deleted, 1-Normal',
    is_automatic INT      NOT NULL DEFAULT 0 COMMENT '自动推荐，0-手动，1-自动',
    PRIMARY KEY (id)
) COMMENT = '分类';

DROP TABLE IF EXISTS comment;
CREATE TABLE comment
(
    id         INT          NOT NULL AUTO_INCREMENT COMMENT 'id',
    content    VARCHAR(900) NOT NULL COMMENT '评论内容',
    created_by INT          NOT NULL COMMENT '评论用户id',
    reply_to   INT COMMENT '回复用户id',
    article_id INT          NOT NULL COMMENT '评论文章id',
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    status     INT          NOT NULL DEFAULT 1 COMMENT '状态, 0-Deleted, 1-Normal',
    updated_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id)
) COMMENT = '评论';

DROP TABLE IF EXISTS user_like;
CREATE TABLE user_like
(
    id         INT      NOT NULL AUTO_INCREMENT COMMENT 'id',
    user_id    INT      NOT NULL COMMENT '用户id',
    article_id INT      NOT NULL COMMENT '文章id',
    status     INT      NOT NULL DEFAULT 1 COMMENT '状态, 0-Deleted, 1-Normal',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id)
) COMMENT = '用户点赞';


CREATE UNIQUE INDEX user_article ON user_like (user_id, article_id);

DROP TABLE IF EXISTS user_fav;
CREATE TABLE user_fav
(
    id         INT      NOT NULL AUTO_INCREMENT COMMENT 'id',
    user_id    INT      NOT NULL COMMENT '用户id',
    article_id INT      NOT NULL COMMENT '文章id',
    status     INT      NOT NULL DEFAULT 1 COMMENT '状态, 0-Deleted, 1-Normal',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id)
) COMMENT = '用户收藏';


CREATE UNIQUE INDEX user_article ON user_fav (user_id, article_id);

DROP TABLE IF EXISTS share;
CREATE TABLE share
(
    id         INT      NOT NULL AUTO_INCREMENT COMMENT 'id',
    user_id    INT      NOT NULL COMMENT '用户ID',
    article_id INT      NOT NULL COMMENT '文章ID',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id)
) COMMENT = '分享';

DROP TABLE IF EXISTS third_part_user;
CREATE TABLE third_part_user
(
    id         INT          NOT NULL AUTO_INCREMENT COMMENT 'id',
    user_id    INT          NOT NULL COMMENT '用户ID',
    open_id    VARCHAR(255) NOT NULL COMMENT '三方ID',
    type       INT          NOT NULL COMMENT '三方账号类型，1苹果，2微信，3微博',
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id)
) COMMENT = '三方账号表';

DROP TABLE IF EXISTS app_version;
CREATE TABLE app_version
(
    id                 INT          NOT NULL AUTO_INCREMENT COMMENT '自增id',
    app_name           VARCHAR(255) NOT NULL COMMENT 'app名称',
    app_version        INT          NOT NULL COMMENT 'app版本',
    force_update       INT COMMENT '强制更新',
    compatible_version INT COMMENT '兼容版本',
    description        VARCHAR(255) COMMENT '描述',
    download_link      VARCHAR(255) COMMENT '下载链接',
    create_by          INT COMMENT '创建人',
    status             INT          NOT NULL DEFAULT 1 COMMENT '状态, 0-Deleted, 1-Normal',
    created_at         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id)
) COMMENT = 'app版本';

DROP TABLE IF EXISTS hot_search;
CREATE TABLE hot_search
(
    id         INT          NOT NULL AUTO_INCREMENT COMMENT '自增id',
    keyword    VARCHAR(255) NOT NULL COMMENT '热搜关键字',
    sort       INT          NOT NULL DEFAULT 0 COMMENT '排序',
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id)
) COMMENT = '热搜表';
