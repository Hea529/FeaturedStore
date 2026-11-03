# FeaturedStore 精选商城项目

> 前后端分离商城系统

## 项目模块
- 🖥️ Featured_Store_Server：SpringBoot 3 多模块后端服务（含 `sql/init_ddl.sql` 数据库脚本）

## 技术栈
后端：SpringBoot 3.3 + Maven 多模块 + MyBatis-Plus + JWT
数据库：MySQL 8（库名 `uni_app`）
缓存：Redis

## 启动说明
1. 创建数据库 `uni_app` 并导入 `Featured_Store_Server/sql/init_ddl.sql`
2. 配置环境变量（可选，默认值仅用于本地开发）：`DB_USERNAME` / `DB_PASSWORD` / `JWT_ACCESS_SECRET` / `JWT_REFRESH_SECRET` / `WECHAT_APPID` / `WECHAT_SECRET`
3. 启动后端：`mvn spring-boot:run -pl mall-bootstrap`（默认端口 8080）
4. 默认管理员：`admin / admin123`

## 仓库地址
https://github.com/Hea529/FeaturedStore.git
