DROP TABLE IF EXISTS crawler_rule;
CREATE TABLE crawler_rule
(
    id            INT          NOT NULL AUTO_INCREMENT COMMENT '自增id',
    name          VARCHAR(255) NOT NULL COMMENT '规则名称',
    host          VARCHAR(255) NOT NULL COMMENT 'host',
    type          INT          NOT NULL DEFAULT 1 COMMENT '爬取类型, 默认 1-文章',
    target_rule   TEXT         NOT NULL COMMENT '爬取目标规则',
    exclude_rule  TEXT COMMENT '剔除元素规则',
    beautify_rule TEXT COMMENT '美化元素规则',
    cover         TEXT         NOT NULL COMMENT '封面',
    note          TEXT COMMENT '备注',
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    status        INT          NOT NULL DEFAULT 1 COMMENT '状态, 0-Deleted, 1-Normal',
    PRIMARY KEY (id)
) COMMENT = '爬虫规则表'