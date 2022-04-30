DROP TABLE IF EXISTS movie;
CREATE TABLE movie
(
    id         INT          NOT NULL AUTO_INCREMENT COMMENT '自增id',
    imdbId     VARCHAR(20) COMMENT 'imdb ID',
    title      VARCHAR(255) NOT NULL COMMENT '标题',
    year       INT          NOT NULL COMMENT '上映年份',
    address    TEXT         NOT NULL COMMENT '视频地址',
    intro      TEXT COMMENT '介绍',
    cover      TEXT         NOT NULL COMMENT '封面',
    head_cover TEXT COMMENT '横板封面',
    detail     TEXT         NOT NULL COMMENT '详情数据集',
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    status     INT          NOT NULL DEFAULT 1 COMMENT '状态, 0-Deleted, 1-Normal',
    PRIMARY KEY (id),
    CONSTRAINT title UNIQUE (imdbId)
) COMMENT = '电影库'
    ENGINE = InnoDB
    DEFAULT CHARSET = utf8mb4
    COLLATE = utf8mb4_0900_ai_ci;