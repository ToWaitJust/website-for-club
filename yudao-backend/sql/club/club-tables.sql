-- ============================================
-- AI+CLUB 自定义业务表
-- 使用方式: Navicat 打开 web_for_club 数据库,
--           直接运行本文件即可。
-- ============================================

-- 1. 业务线
CREATE TABLE IF NOT EXISTS `club_business_line` (
    `id`           bigint    NOT NULL AUTO_INCREMENT COMMENT '编号',
    `slug`         varchar(64)  NOT NULL COMMENT '业务线标识',
    `name`         varchar(64)  NOT NULL COMMENT '业务线名称',
    `tagline`      varchar(255) NOT NULL DEFAULT '' COMMENT '一句话简介',
    `content_md`   text         COMMENT '简介 Markdown',
    `register_open` bit(1) NOT NULL DEFAULT b'0' COMMENT '报名是否开放',
    `sort`         int          NOT NULL DEFAULT 0 COMMENT '排序',
    `creator`      varchar(64)  DEFAULT '' COMMENT '创建者',
    `create_time`  datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`      varchar(64)  DEFAULT '' COMMENT '更新者',
    `update_time`  datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`      bit(1)       NOT NULL DEFAULT b'0' COMMENT '是否删除',
    `tenant_id`    bigint       NOT NULL DEFAULT 0 COMMENT '租户编号',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `uk_slug` (`slug`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='业务线';

-- 2. 报名记录
CREATE TABLE IF NOT EXISTS `club_register` (
    `id`            bigint    NOT NULL AUTO_INCREMENT COMMENT '编号',
    `business_line` varchar(64)  NOT NULL DEFAULT '' COMMENT '业务线 slug',
    `name`          varchar(64)  NOT NULL DEFAULT '' COMMENT '姓名',
    `student_id`    varchar(64)  NOT NULL DEFAULT '' COMMENT '学号',
    `college`       varchar(64)  NOT NULL DEFAULT '' COMMENT '学院',
    `major`         varchar(64)  NOT NULL DEFAULT '' COMMENT '专业',
    `phone`         varchar(32)  NOT NULL DEFAULT '' COMMENT '手机号',
    `wechat`        varchar(64)  NOT NULL DEFAULT '' COMMENT '微信号',
    `motivation`    text         COMMENT '报名动机',
    `status`        tinyint      NOT NULL DEFAULT 0 COMMENT '状态:0=待处理 1=已联系 2=已录取 3=未录取',
    `creator`       varchar(64)  DEFAULT '' COMMENT '创建者',
    `create_time`   datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`       varchar(64)  DEFAULT '' COMMENT '更新者',
    `update_time`   datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`       bit(1)       NOT NULL DEFAULT b'0' COMMENT '是否删除',
    `tenant_id`     bigint       NOT NULL DEFAULT 0 COMMENT '租户编号',
    PRIMARY KEY (`id`) USING BTREE,
    KEY `idx_business_line` (`business_line`) USING BTREE,
    KEY `idx_status` (`status`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='报名记录';

-- 3. 反馈记录
CREATE TABLE IF NOT EXISTS `club_feedback` (
    `id`          bigint    NOT NULL AUTO_INCREMENT COMMENT '编号',
    `page`        varchar(64)  NOT NULL DEFAULT '' COMMENT '来源页面标识',
    `name`        varchar(64)  NOT NULL DEFAULT '' COMMENT '姓名',
    `content`     text         NOT NULL COMMENT '反馈内容',
    `status`      tinyint      NOT NULL DEFAULT 0 COMMENT '状态:0=待处理 1=已读',
    `creator`     varchar(64)  DEFAULT '' COMMENT '创建者',
    `create_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`     varchar(64)  DEFAULT '' COMMENT '更新者',
    `update_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`     bit(1)       NOT NULL DEFAULT b'0' COMMENT '是否删除',
    `tenant_id`   bigint       NOT NULL DEFAULT 0 COMMENT '租户编号',
    PRIMARY KEY (`id`) USING BTREE,
    KEY `idx_status` (`status`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='反馈记录';

-- 4. 公告
CREATE TABLE IF NOT EXISTS `club_notice` (
    `id`           bigint    NOT NULL AUTO_INCREMENT COMMENT '编号',
    `title`        varchar(128) NOT NULL DEFAULT '' COMMENT '标题',
    `content`      text         NOT NULL COMMENT '内容',
    `publish_time` datetime     DEFAULT NULL COMMENT '发布时间',
    `status`       tinyint      NOT NULL DEFAULT 0 COMMENT '状态:1=发布 0=下架',
    `creator`      varchar(64)  DEFAULT '' COMMENT '创建者',
    `create_time`  datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`      varchar(64)  DEFAULT '' COMMENT '更新者',
    `update_time`  datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`      bit(1)       NOT NULL DEFAULT b'0' COMMENT '是否删除',
    `tenant_id`    bigint       NOT NULL DEFAULT 0 COMMENT '租户编号',
    PRIMARY KEY (`id`) USING BTREE,
    KEY `idx_status` (`status`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='公告';