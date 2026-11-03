-- ============================================================
-- Preferred Mall 多模块重构版 - 数据库初始化脚本
-- 目标库：uni_app   MySQL 8.0.x   utf8mb4
-- 注意：本脚本会清空原表数据，适合全新建库或重置开发环境
-- ============================================================

-- 切换到本机已有库；若要全新建库，取消下一行注释
-- CREATE DATABASE IF NOT EXISTS uni_app DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
-- USE uni_app;

SET FOREIGN_KEY_CHECKS = 0;

-- ============ 清理 ============
DROP TABLE IF EXISTS coupon_order_rel;
DROP TABLE IF EXISTS coupon_user;
DROP TABLE IF EXISTS coupon_scope_detail;
DROP TABLE IF EXISTS coupon_mutex_group;
DROP TABLE IF EXISTS coupon;
DROP TABLE IF EXISTS product_comment_like;
DROP TABLE IF EXISTS product_comment_append;
DROP TABLE IF EXISTS product_comment;
DROP TABLE IF EXISTS product_image;
DROP TABLE IF EXISTS product_spec;
DROP TABLE IF EXISTS product;
DROP TABLE IF EXISTS category;
DROP TABLE IF EXISTS product_search_keyword;
DROP TABLE IF EXISTS collection;
DROP TABLE IF EXISTS order_tracking;
DROP TABLE IF EXISTS order_item;
DROP TABLE IF EXISTS `order`;
DROP TABLE IF EXISTS cart;
DROP TABLE IF EXISTS address;
DROP TABLE IF EXISTS chat_message;
DROP TABLE IF EXISTS chat_session;
DROP TABLE IF EXISTS banner;
DROP TABLE IF EXISTS notice;
DROP TABLE IF EXISTS faq;
DROP TABLE IF EXISTS feedback;
DROP TABLE IF EXISTS factory_info;
DROP TABLE IF EXISTS mq_consumer_failed_msg;
DROP TABLE IF EXISTS operation_log;
DROP TABLE IF EXISTS consult_unuseable;
DROP TABLE IF EXISTS payment_record;
DROP TABLE IF EXISTS refund_record;

-- ============ mall-user ============
CREATE TABLE sys_user (
  id                BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  username          VARCHAR(64)     NULL     COMMENT '账号（管理员登录用）',
  password          VARCHAR(128)    NULL     COMMENT 'BCrypt 加密密码',
  openid            VARCHAR(128)    NULL     COMMENT '微信 openid（唯一）',
  phone             VARCHAR(20)     NULL,
  nickname          VARCHAR(64)     NULL,
  avatar            VARCHAR(512)    NULL,
  user_type         TINYINT         NOT NULL DEFAULT 3 COMMENT '1 系统管理员 2 普通管理员 3 普通买家',
  is_enable         TINYINT         NOT NULL DEFAULT 1 COMMENT '1 启用 / 0 禁用',
  shop_auth_status  TINYINT         NOT NULL DEFAULT 0 COMMENT '0 未认证 / 1 已认证',
  first_login_time  DATETIME        NULL,
  last_login_time   DATETIME        NULL,
  create_time       DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time       DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_username (username),
  UNIQUE KEY uk_openid   (openid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表（管理员与买家共用）';

CREATE TABLE sys_role (
  id           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  role_code    VARCHAR(64)     NOT NULL COMMENT 'ROLE_ADMIN / ROLE_USER ...',
  role_name    VARCHAR(64)     NOT NULL,
  description  VARCHAR(255)    NULL,
  is_enable    TINYINT         NOT NULL DEFAULT 1,
  sort         INT             NOT NULL DEFAULT 0,
  create_time  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_role_code (role_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色';

CREATE TABLE sys_permission (
  id           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  parent_id    BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '0 顶级',
  perm_code    VARCHAR(128)    NOT NULL COMMENT 'user:update / admin:product:list',
  perm_name    VARCHAR(64)     NOT NULL,
  perm_type    TINYINT         NOT NULL COMMENT '1 菜单 / 2 接口',
  path         VARCHAR(255)    NULL COMMENT '菜单 URL 或接口路径',
  icon         VARCHAR(64)     NULL,
  sort         INT             NOT NULL DEFAULT 0,
  is_enable    TINYINT         NOT NULL DEFAULT 1,
  create_time  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_perm_code (perm_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限';

CREATE TABLE sys_user_role (
  user_id BIGINT UNSIGNED NOT NULL,
  role_id BIGINT UNSIGNED NOT NULL,
  PRIMARY KEY (user_id, role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户-角色';

CREATE TABLE sys_role_permission (
  role_id BIGINT UNSIGNED NOT NULL,
  perm_id BIGINT UNSIGNED NOT NULL,
  PRIMARY KEY (role_id, perm_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色-权限';

CREATE TABLE address (
  id               BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  user_id          BIGINT UNSIGNED NOT NULL,
  receiver         VARCHAR(64)     NOT NULL,
  phone            VARCHAR(20)     NOT NULL,
  province_id      BIGINT UNSIGNED NULL,
  city_id          BIGINT UNSIGNED NULL,
  district_id      BIGINT UNSIGNED NULL,
  province_name    VARCHAR(64)     NULL,
  city_name        VARCHAR(64)     NULL,
  district_name    VARCHAR(64)     NULL,
  detail_address   VARCHAR(255)    NOT NULL,
  is_default       TINYINT         NOT NULL DEFAULT 0,
  create_time      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  KEY idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收货地址';

-- ============ mall-product ============
CREATE TABLE category (
  id        BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  parent_id BIGINT UNSIGNED NOT NULL DEFAULT 0,
  name      VARCHAR(64)     NOT NULL,
  icon_url  VARCHAR(512)    NULL,
  sort      INT             NOT NULL DEFAULT 0,
  status    TINYINT         NOT NULL DEFAULT 1,
  KEY idx_parent (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分类';

CREATE TABLE product (
  id             BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  name           VARCHAR(128)    NOT NULL,
  subtitle       VARCHAR(255)    NULL,
  cover_image    VARCHAR(512)    NULL,
  detail         MEDIUMTEXT      NULL,
  category_id    BIGINT UNSIGNED NOT NULL,
  price          DECIMAL(10,2)   NOT NULL DEFAULT 0,
  enterprise_price DECIMAL(10,2) NULL,
  stock          INT             NOT NULL DEFAULT 0 COMMENT '所有规格库存之和（冗余聚合）',
  sales_count    INT             NOT NULL DEFAULT 0,
  view_count     INT             NOT NULL DEFAULT 0,
  status         TINYINT         NOT NULL DEFAULT 1 COMMENT '1 上架 / 0 下架',
  create_time    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  KEY idx_category_id (category_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品 SPU';

CREATE TABLE product_spec (
  id               BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  product_id       BIGINT UNSIGNED NOT NULL,
  spec_text        VARCHAR(128)    NOT NULL COMMENT '白色/L码',
  price            DECIMAL(10,2)   NOT NULL,
  enterprise_price DECIMAL(10,2)   NULL,
  stock            INT             NOT NULL DEFAULT 0,
  KEY idx_product_id (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品规格 SKU';

CREATE TABLE product_image (
  id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  product_id  BIGINT UNSIGNED NOT NULL,
  image_url   VARCHAR(512)    NOT NULL,
  sort        INT             NOT NULL DEFAULT 0,
  KEY idx_product_id (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品图集';

CREATE TABLE collection (
  id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  user_id     BIGINT UNSIGNED NOT NULL,
  product_id  BIGINT UNSIGNED NOT NULL,
  create_time DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_user_product (user_id, product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品收藏';

CREATE TABLE product_search_keyword (
  id        BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  keyword   VARCHAR(64)     NOT NULL,
  use_count BIGINT UNSIGNED NOT NULL DEFAULT 0,
  is_hot    TINYINT         NOT NULL DEFAULT 0,
  is_show   TINYINT         NOT NULL DEFAULT 1,
  update_time DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_keyword (keyword)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='搜索关键词';

CREATE TABLE product_comment (
  id           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  product_id   BIGINT UNSIGNED NOT NULL,
  parent_id    BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '0 一级 / >0 回复',
  reply_user_id BIGINT UNSIGNED NULL,
  user_id      BIGINT UNSIGNED NOT NULL,
  user_nickname VARCHAR(64)    NULL,
  user_avatar  VARCHAR(512)   NULL,
  rating       TINYINT         NULL COMMENT '1-5（仅一级）',
  content      VARCHAR(1000)   NOT NULL,
  image_urls   VARCHAR(2000)   NULL,
  anonymous    TINYINT         NOT NULL DEFAULT 0,
  is_deleted   TINYINT         NOT NULL DEFAULT 0,
  bought_flag  TINYINT         NOT NULL DEFAULT 0,
  like_count   INT             NOT NULL DEFAULT 0,
  status       TINYINT         NOT NULL DEFAULT 1 COMMENT '0 待审 / 1 通过 / 2 驳回',
  create_time  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY idx_product_parent (product_id, parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品评论主表';

CREATE TABLE product_comment_append (
  id           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  comment_id   BIGINT UNSIGNED NOT NULL COMMENT '对应一级评论',
  content      VARCHAR(1000)   NOT NULL,
  image_urls   VARCHAR(2000)   NULL,
  status       TINYINT         NOT NULL DEFAULT 1,
  create_time  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_comment_id (comment_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品评论追评';

CREATE TABLE product_comment_like (
  comment_id BIGINT UNSIGNED NOT NULL,
  user_id    BIGINT UNSIGNED NOT NULL,
  status     TINYINT         NOT NULL DEFAULT 1 COMMENT '1 点赞 / 0 取消',
  update_time DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (comment_id, user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品评论点赞';

-- ============ mall-order ============
CREATE TABLE cart (
  id         BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  user_id    BIGINT UNSIGNED NOT NULL,
  product_id BIGINT UNSIGNED NOT NULL,
  spec_id    BIGINT UNSIGNED NOT NULL,
  quantity   INT             NOT NULL DEFAULT 1,
  checked    TINYINT         NOT NULL DEFAULT 1,
  UNIQUE KEY uk_user_product_spec (user_id, product_id, spec_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车';

CREATE TABLE `order` (
  id               BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  order_no         VARCHAR(32)     NOT NULL COMMENT '业务订单号（雪花 ID）',
  user_id          BIGINT UNSIGNED NOT NULL,
  address_id       BIGINT UNSIGNED NULL,
  receiver         VARCHAR(64)     NOT NULL,
  phone            VARCHAR(20)     NOT NULL,
  province_name    VARCHAR(64)     NULL,
  city_name        VARCHAR(64)     NULL,
  district_name    VARCHAR(64)     NULL,
  detail_address   VARCHAR(255)    NOT NULL,
  total_amount     DECIMAL(10,2)   NOT NULL,
  discount_amount  DECIMAL(10,2)   NOT NULL DEFAULT 0,
  freight_amount   DECIMAL(10,2)   NOT NULL DEFAULT 0,
  pay_amount       DECIMAL(10,2)   NOT NULL,
  pay_type         TINYINT         NOT NULL DEFAULT 1,
  status           TINYINT         NOT NULL DEFAULT 1 COMMENT '1 待付 / 2 已付 / 3 已发 / 4 完成 / 5 取消 / 6 售后',
  logistics_company VARCHAR(64)   NULL,
  logistics_no     VARCHAR(64)     NULL,
  pay_time         DATETIME        NULL,
  ship_time        DATETIME        NULL,
  finish_time      DATETIME        NULL,
  cancel_time      DATETIME        NULL,
  remark           VARCHAR(255)    NULL,
  is_deleted       TINYINT         NOT NULL DEFAULT 0,
  create_time      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_order_no (order_no),
  KEY idx_user_status (user_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单';

CREATE TABLE order_item (
  id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  order_id    BIGINT UNSIGNED NOT NULL,
  product_id  BIGINT UNSIGNED NOT NULL,
  spec_id     BIGINT UNSIGNED NOT NULL,
  product_name VARCHAR(128)  NOT NULL,
  cover_image VARCHAR(512)   NULL,
  spec_text   VARCHAR(128)   NULL,
  price       DECIMAL(10,2) NOT NULL,
  quantity    INT           NOT NULL,
  subtotal    DECIMAL(10,2) NOT NULL,
  KEY idx_order_id (order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单明细';

CREATE TABLE order_tracking (
  id               BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  order_id         BIGINT UNSIGNED NOT NULL,
  logistics_no     VARCHAR(64)     NOT NULL COMMENT '注意：原库有唯一约束，这里去掉以支持多节点',
  logistics_company VARCHAR(64)    NULL,
  logistics_status VARCHAR(64)     NULL COMMENT '揽收 / 运输中 / 派送中 / 已签收',
  location         VARCHAR(255)    NULL,
  description      VARCHAR(512)    NULL,
  time             DATETIME        NULL,
  KEY idx_order_id (order_id),
  KEY idx_logistics_no (logistics_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物流轨迹';

-- ============ mall-payment（新增） ============
CREATE TABLE payment_record (
  id               BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  order_no         VARCHAR(32)     NOT NULL,
  transaction_id   VARCHAR(64)     NULL COMMENT '微信支付单号',
  pay_type         TINYINT         NOT NULL DEFAULT 1,
  amount           DECIMAL(10,2)   NOT NULL,
  status           TINYINT         NOT NULL DEFAULT 0 COMMENT '0 待支付 / 1 成功 / 2 失败 / 3 已退款',
  notify_raw       MEDIUMTEXT      NULL COMMENT '原始回调报文',
  pay_time         DATETIME        NULL,
  create_time      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_transaction_id (transaction_id),
  UNIQUE KEY uk_order_no (order_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付流水（原库缺失）';

CREATE TABLE refund_record (
  id               BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  order_no         VARCHAR(32)     NOT NULL,
  transaction_id   VARCHAR(64)     NULL,
  refund_amount    DECIMAL(10,2)   NOT NULL,
  reason           VARCHAR(255)    NULL,
  status           TINYINT         NOT NULL DEFAULT 0 COMMENT '0 申请中 / 1 成功 / 2 失败',
  create_time      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY idx_order_no (order_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='退款流水（原库缺失）';

-- ============ mall-coupon ============
CREATE TABLE coupon_mutex_group (
  id         BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  group_code VARCHAR(64)     NOT NULL,
  group_name VARCHAR(64)     NOT NULL,
  remark     VARCHAR(255)    NULL,
  UNIQUE KEY uk_group_code (group_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='优惠券互斥组';

CREATE TABLE coupon (
  id             BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  coupon_no      VARCHAR(32)     NULL,
  title          VARCHAR(64)     NOT NULL,
  subtitle       VARCHAR(255)    NULL,
  description    VARCHAR(1000)   NULL,
  coupon_type    TINYINT         NOT NULL COMMENT '1 满减 / 2 折扣 / 3 无门槛 / 4 单品',
  min_amount     DECIMAL(10,2)   NOT NULL DEFAULT 0 COMMENT '使用门槛',
  face_value     DECIMAL(10,2)   NULL COMMENT '满减金额（coupon_type=1/4）',
  discount_rate  DECIMAL(5,4)    NULL COMMENT '折扣率（coupon_type=2，如 0.8）',
  total_quota    INT             NOT NULL DEFAULT 0,
  receive_quota  INT             NOT NULL DEFAULT 0,
  used_quota     INT             NOT NULL DEFAULT 0,
  per_limit      INT             NOT NULL DEFAULT 1,
  valid_mode     TINYINT         NOT NULL DEFAULT 1 COMMENT '1 固定时间 / 2 领后 N 天',
  valid_start    DATETIME        NULL,
  valid_end      DATETIME        NULL,
  valid_days     INT             NULL COMMENT 'valid_mode=2 时使用',
  use_scope      TINYINT         NOT NULL DEFAULT 1 COMMENT '1 全店 / 2 分类 / 3 指定商品',
  mutex_group_id BIGINT UNSIGNED NULL,
  status         TINYINT         NOT NULL DEFAULT 1 COMMENT '0 未上架 / 1 已上架 / 2 已下架',
  create_time    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='优惠券模板';

CREATE TABLE coupon_scope_detail (
  id         BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  coupon_id  BIGINT UNSIGNED NOT NULL,
  scope_type TINYINT         NOT NULL COMMENT '1 分类 / 2 商品',
  target_id  BIGINT UNSIGNED NOT NULL,
  KEY idx_coupon_id (coupon_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='优惠券适用范围明细';

CREATE TABLE coupon_user (
  id         BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  user_id    BIGINT UNSIGNED NOT NULL,
  coupon_id  BIGINT UNSIGNED NOT NULL,
  valid_start DATETIME       NOT NULL,
  valid_end  DATETIME        NOT NULL,
  use_status TINYINT         NOT NULL DEFAULT 1 COMMENT '0 未开始 / 1 未用 / 2 已用 / 3 过期 / 4 回滚',
  receive_time DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  use_time   DATETIME        NULL,
  KEY idx_user_id (user_id),
  KEY idx_coupon_id (coupon_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户持券';

CREATE TABLE coupon_order_rel (
  id               BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  order_id         BIGINT UNSIGNED NULL,
  order_no         VARCHAR(32)     NULL,
  order_item_id    BIGINT UNSIGNED NULL,
  user_coupon_id   BIGINT UNSIGNED NOT NULL,
  activity_id      BIGINT UNSIGNED NULL,
  discount_amount  DECIMAL(10,2)   NOT NULL,
  rel_status       TINYINT         NOT NULL DEFAULT 1 COMMENT '1 锁定 / 2 已核销 / 3 已回滚',
  use_time         DATETIME        NULL,
  rollback_time    DATETIME        NULL,
  KEY idx_order_no (order_no),
  KEY idx_user_coupon_id (user_coupon_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单-优惠券核销关联';

-- ============ mall-content ============
CREATE TABLE banner (
  id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  image_url   VARCHAR(512)    NOT NULL,
  link_url    VARCHAR(512)    NULL,
  link_type   TINYINT         NOT NULL DEFAULT 3 COMMENT '1 商品 / 2 分类 / 3 外链',
  sort        INT             NOT NULL DEFAULT 0,
  status      TINYINT         NOT NULL DEFAULT 1,
  create_time DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='首页轮播';

CREATE TABLE notice (
  id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  title       VARCHAR(128)    NOT NULL,
  content     MEDIUMTEXT      NULL,
  status      TINYINT         NOT NULL DEFAULT 1,
  create_time DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公告';

CREATE TABLE faq (
  id         BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  question   VARCHAR(255)    NOT NULL,
  answer     TEXT            NULL,
  category   VARCHAR(64)     NULL,
  sort       INT             NOT NULL DEFAULT 0,
  status     TINYINT         NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='FAQ';

CREATE TABLE factory_info (
  id                  BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  factory_name         VARCHAR(128)    NULL,
  description         VARCHAR(1000)   NULL,
  bg_image_url        VARCHAR(512)    NULL,
  content             MEDIUMTEXT      NULL,
  service_hotline     VARCHAR(32)     NULL,
  official_wechat     VARCHAR(64)     NULL,
  address             VARCHAR(255)    NULL,
  copyright_info      VARCHAR(255)    NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工厂/品牌信息';

CREATE TABLE feedback (
  id             BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  user_id        BIGINT UNSIGNED NULL,
  content        VARCHAR(1000)   NOT NULL,
  image_urls     VARCHAR(2000)   NULL,
  contact        VARCHAR(64)     NULL,
  status         TINYINT         NOT NULL DEFAULT 0,
  reply_content  VARCHAR(1000)   NULL,
  reply_admin_id BIGINT UNSIGNED NULL,
  create_time    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
  reply_time     DATETIME        NULL,
  KEY idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户反馈';

-- ============ mall-im ============
CREATE TABLE chat_session (
  id              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  user_id         BIGINT UNSIGNED NOT NULL COMMENT '较小 ID',
  contact_id      BIGINT UNSIGNED NOT NULL COMMENT '较大 ID',
  last_msg_content VARCHAR(512)   NULL,
  last_msg_time   DATETIME        NULL,
  unread_count_a  INT             NOT NULL DEFAULT 0,
  unread_count_b  INT             NOT NULL DEFAULT 0,
  create_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_pair (user_id, contact_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='聊天会话';

CREATE TABLE chat_message (
  id         BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  session_id BIGINT UNSIGNED NOT NULL,
  from_user_id BIGINT UNSIGNED NOT NULL,
  to_user_id BIGINT UNSIGNED NOT NULL,
  msg_type   TINYINT         NOT NULL DEFAULT 1 COMMENT '1 文本 / 2 图片 / 3 商品卡片 / 4 语音',
  content    VARCHAR(1000)   NULL,
  product_id BIGINT UNSIGNED NULL,
  is_read    TINYINT         NOT NULL DEFAULT 0,
  create_time DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY idx_session_id (session_id),
  KEY idx_to_user_id (to_user_id, is_read)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='聊天消息';

-- ============ mall-infra ============
CREATE TABLE operation_log (
  id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  admin_id    BIGINT UNSIGNED NULL,
  operation   VARCHAR(128)    NULL,
  operation_url VARCHAR(255)  NULL,
  operation_method VARCHAR(16) NULL,
  request_params TEXT           NULL,
  ip          VARCHAR(64)     NULL,
  status      TINYINT         NOT NULL DEFAULT 1,
  cost_ms     INT             NULL,
  create_time DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作审计日志';

CREATE TABLE mq_consumer_failed_msg (
  id             BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  msg_id         VARCHAR(128)    NULL,
  biz_id         VARCHAR(64)     NULL,
  topic          VARCHAR(64)     NULL,
  tag            VARCHAR(64)     NULL,
  body           TEXT            NULL,
  consumer_class VARCHAR(128)    NULL,
  error_msg      VARCHAR(2000)   NULL,
  status         TINYINT         NOT NULL DEFAULT 0 COMMENT '0 待处理 / 1 已处理 / 2 无需处理',
  retry_count    INT             NOT NULL DEFAULT 0,
  create_time    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='MQ 消费失败落库';

SET FOREIGN_KEY_CHECKS = 1;

-- ============ 初始化数据 ============
-- 注意：admin 密码为 BCryptPasswordEncoder.encode("admin123") 的真实哈希（已用 spring-security-crypto 6.3.0 生成并校验 matches("admin123")=true）。

INSERT INTO sys_role (id, role_code, role_name, description, is_enable, sort) VALUES
(1, 'ROLE_ADMIN',  '系统管理员', '管理端全部权限',   1, 1),
(2, 'ROLE_USER',   '普通买家',   '小程序买家',       1, 2);

INSERT INTO sys_permission (id, parent_id, perm_code, perm_name, perm_type, path, sort, is_enable) VALUES
(1, 0, 'admin:product:list',   '商品列表',   2, '/admin/product/list',   1, 1),
(2, 0, 'admin:product:save',   '商品新增',   2, '/admin/product',         2, 1),
(3, 0, 'admin:product:update', '商品修改',   2, '/admin/product/{id}',    3, 1),
(4, 0, 'admin:coupon:list',    '优惠券列表', 2, '/admin/coupon/list',     4, 1),
(5, 0, 'admin:coupon:save',    '优惠券新增', 2, '/admin/coupon',          5, 1),
(6, 0, 'admin:order:list',      '订单列表',   2, '/admin/order/list',      6, 1),
(7, 0, 'admin:order:ship',     '订单发货',   2, '/admin/order/{id}/ship', 7, 1);

INSERT INTO sys_role_permission (role_id, perm_id) VALUES
(1,1),(1,2),(1,3),(1,4),(1,5),(1,6),(1,7);

-- admin 账号：username=admin, 密码 admin123（BCrypt 哈希，已校验可匹配）
INSERT INTO sys_user (id, username, password, nickname, avatar, user_type, is_enable) VALUES
(1, 'admin', '$2a$10$konOBmbDc0JILu/OPJWte.rzWOKJO11N8We6pO9qeEP0wV6QxQ8ky', '超级管理员', '', 1, 1);

INSERT INTO sys_user_role (user_id, role_id) VALUES (1, 1);

-- 示例分类
INSERT INTO category (id, parent_id, name, icon_url, sort, status) VALUES
(1, 0, '地板',    '', 1, 1),
(2, 0, '瓷砖',    '', 2, 1),
(3, 0, '卫浴',    '', 3, 1),
(4, 0, '涂料',    '', 4, 1),
(101, 1, '实木地板',   '', 1, 1),
(102, 1, '复合地板',   '', 2, 1),
(103, 2, '通体大理石', '', 1, 1),
(104, 2, '岩板',       '', 2, 1),
(105, 3, '花洒',       '', 1, 1),
(106, 3, '马桶',       '', 2, 1);

-- 示例商品（3 件）
INSERT INTO product (id, name, subtitle, cover_image, detail, category_id, price, enterprise_price, stock, sales_count, view_count, status) VALUES
(1, '橡木实木地板 900×120', 'ENF 级环保 / 拉丝工艺', '', '示例详情 HTML', 101, 289.00, 259.00, 500, 0, 0, 1),
(2, '岩板背景墙 3600×1200', '连纹通铺 / 岩胚质感', '', '示例详情 HTML', 104, 589.00, 530.00, 120, 0, 0, 1),
(3, '恒温花洒 哑光黑',      '金属主体 / 恒温阀芯', '', '示例详情 HTML', 105, 699.00, 629.00, 300, 0, 0, 1);

INSERT INTO product_spec (product_id, spec_text, price, enterprise_price, stock) VALUES
(1, '浅咖 / 900×120',    289.00, 259.00, 300),
(1, '深咖 / 900×120',    299.00, 269.00, 200),
(2, '卡拉拉白 / 3.6m',   589.00, 530.00, 80),
(2, '鱼肚白   / 3.6m',   589.00, 530.00, 40),
(3, '哑光黑',            699.00, 629.00, 300);

INSERT INTO product_image (product_id, image_url, sort) VALUES
(1, 'https://example.com/p1a.jpg', 1),
(1, 'https://example.com/p1b.jpg', 2),
(2, 'https://example.com/p2a.jpg', 1),
(3, 'https://example.com/p3a.jpg', 1);

-- 示例优惠券：满 1000 减 100
INSERT INTO coupon (id, coupon_no, title, description, coupon_type, min_amount, face_value, total_quota, receive_quota, used_quota, per_limit, valid_mode, valid_start, valid_end, use_scope, status) VALUES
(1, 'CPN20261102001', '满 1000 减 100', '首次下单专享', 1, 1000.00, 100.00, 1000, 0, 0, 1, 1, '2026-11-01 00:00:00', '2027-01-31 23:59:59', 1, 1);

-- banner
INSERT INTO banner (image_url, link_url, link_type, sort, status) VALUES
('https://example.com/banner1.jpg', '/api/product/list?categoryId=1', 1, 1, 1),
('https://example.com/banner2.jpg',  'https://www.example.com',       3, 2, 1);

-- 公告
INSERT INTO notice (title, content, status) VALUES
('欢迎使用 Preferred Mall', '这是商城的第一条公告', 1);

-- 工厂信息（唯一一条）
INSERT INTO factory_info (id, factory_name, description, service_hotline, official_wechat, address, copyright_info) VALUES
(1, 'Preferred Mall 示范商城', '建材家居线上批发平台', '400-000-0000', 'preferred_mall', '中国 · 深圳', '© 2026 Preferred Mall All Rights Reserved');
