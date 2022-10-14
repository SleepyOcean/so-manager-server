DROP TABLE IF EXISTS article_reading;
CREATE TABLE article_reading
(
    id             INT          NOT NULL AUTO_INCREMENT COMMENT '自增id',
    md5            VARCHAR(32)  NOT NULL COMMENT 'url的MD5值',
    title          VARCHAR(255) NOT NULL COMMENT '文章标题',
    host           VARCHAR(255) NOT NULL COMMENT 'host',
    source         TEXT         NOT NULL COMMENT '文章源地址',
    content        MEDIUMTEXT   NOT NULL COMMENT '文章内容',
    intro          TEXT COMMENT '文章简介',
    cover          VARCHAR(255) COMMENT '文章封面',
    note           TEXT COMMENT '备注',
    reading_status INT          NOT NULL DEFAULT 1 COMMENT '阅读状态, 0-未阅读, 1-正在阅读, 2-已阅读',
    created_at     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    status         INT          NOT NULL DEFAULT 1 COMMENT '状态, 0-Deleted, 1-Normal',
    PRIMARY KEY (id)
) COMMENT = '稍后阅读表'