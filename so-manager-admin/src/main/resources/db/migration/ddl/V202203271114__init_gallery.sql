-- so.so_gallery definition
DROP TABLE IF EXISTS so_gallery;
CREATE TABLE so_gallery
(
    id          varchar(32)  NOT NULL,
    size        varchar(32)  NOT NULL COMMENT '图片大小',
    format      varchar(32)  NOT NULL COMMENT '图片格式',
    path        varchar(255) NOT NULL COMMENT '图片相对路径',
    resolution  varchar(255) NOT NULL COMMENT '图片分辨率',
    title       varchar(255) NOT NULL COMMENT '图片标题',
    description varchar(1024) DEFAULT NULL COMMENT '图片简述',
    tag         varchar(1024) DEFAULT NULL COMMENT '图片标签',
    create_time datetime     NOT NULL COMMENT '原始图片创建时间',
    upload_time datetime     NOT NULL COMMENT '上传时间',
    PRIMARY KEY (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;