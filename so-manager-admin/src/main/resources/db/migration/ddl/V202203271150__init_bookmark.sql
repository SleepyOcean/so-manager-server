DROP TABLE IF EXISTS bookmark;
CREATE TABLE bookmark
(
    id         INT          NOT NULL AUTO_INCREMENT COMMENT '自增id',
    title      VARCHAR(255) NOT NULL COMMENT '标题',
    address    TEXT COMMENT '地址',
    intro      TEXT         NOT NULL COMMENT '介绍',
    cover      TEXT COMMENT '封面',
    icon       TEXT COMMENT '图标',
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    created_by INT          NOT NULL COMMENT '创建人',
    status     INT          NOT NULL DEFAULT 1 COMMENT '文章状态, 0-Deleted, 1-Normal',
    PRIMARY KEY (id),
    CONSTRAINT title UNIQUE (title)
) COMMENT = '书签';