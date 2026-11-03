# Featured_Store_Server

优选商城后端服务，基于 Spring Boot 3 的 Maven 多模块单体应用。

## 技术栈

| 技术 | 版本 | 说明 |
|---|---|---|
| JDK | 17 | Amazon Corretto 17 |
| Spring Boot | 3.3.0 | Jakarta EE 规范（无 javax 依赖） |
| MyBatis-Plus | 3.5.6 | ORM，逻辑删除、自动填充 |
| MySQL | 8.x | 默认库名 `uni_app` |
| Redis | 7.x（Redisson 3.37.0） | 缓存 / 分布式锁，默认端口 6379 |
| JJWT | 0.12.6 | 登录令牌（access + refresh 双密钥） |
| Knife4j | 4.5.0（底层 springdoc 2.5.0） | 中文接口文档 |
| MapStruct + Lombok | — | DTO/VO 映射、样板代码生成 |

## 模块结构

| 模块 | 职责 |
|---|---|
| mall-bootstrap | 启动模块：主类 `UniAppApplication` + application.yml + 打包 |
| mall-common | 通用层：Result 统一返回、全局异常、枚举、上下文 |
| mall-infra | 基础设施：Redis、MQ、配置属性 |
| mall-security | 安全层：JWT 过滤器、跨域配置、白名单 |
| mall-user | 用户：注册登录、用户信息、收货地址 |
| mall-product | 商品：商品/分类管理、查询、评论、收藏 |
| mall-order | 订单：下单、取消、支付预下单、后台发货 |
| mall-payment | 支付：微信支付结果回调（幂等） |
| mall-coupon | 优惠券：领券、锁定/核销/回滚（关联订单号） |
| mall-content | 内容：轮播图、公告、常见问题、意见反馈 |
| mall-im | 聊天：会话、消息收发、已读 |

依赖关系：各业务模块依赖 `mall-common` / `mall-infra` / `mall-security`，`mall-bootstrap` 聚合所有模块统一启动。

## 环境要求

- JDK 17+
- Maven 3.6+
- MySQL 8（创建数据库 `uni_app`，字符集 utf8mb4）
- Redis 7（本地 6379，无密码）

## 配置说明

配置文件：[mall-bootstrap/src/main/resources/application.yml](mall-bootstrap/src/main/resources/application.yml)

| 配置项 | 环境变量 | 默认值                                   |
|---|---|---------------------------------------|
| 数据库地址 | — | `jdbc:mysql://localhost:3306/uni_app` |
| 数据库账号 | `DB_USERNAME` | `*`                                   |
| 数据库密码 | `DB_PASSWORD` | `*`                                   |
| Redis | — | `localhost:6379`，db 0                 |
| JWT access 密钥 | `JWT_ACCESS_SECRET` | 开发占位值，**生产必须覆盖**                      |
| JWT refresh 密钥 | `JWT_REFRESH_SECRET` | 开发占位值，**生产必须覆盖**                      |
| 微信小程序 appid | `WECHAT_APPID` | 空（未配置时用开发桩换 openid，**禁止生产**）          |
| 微信小程序 secret | `WECHAT_SECRET` | 空                                     |

## 启动

方式一（IDE）：运行主类 `com.preferredmall.UniAppApplication`（位于 mall-bootstrap 模块）。

方式二（命令行）：

```bash
mvn spring-boot:run -pl mall-bootstrap -am
```

启动成功后服务监听 **8080** 端口。

## 接口文档

- **Knife4j（推荐）**：http://localhost:8080/doc.html —— 中文界面，支持在线调试
- Swagger UI（备用）：http://localhost:8080/swagger-ui/index.html
- OpenAPI JSON：http://localhost:8080/v3/api-docs

接口按业务分 15 个中文分组：认证授权、用户中心、地址管理、商品管理（后台）、商品查询（前台）、分类管理、商品评论、商品收藏、我的订单、订单支付、订单管理（后台）、支付回调、优惠券、内容资讯、聊天消息。

## 约定

- **统一返回**：所有接口返回 `Result` 结构（code / msg / data），异常由全局异常处理器兜底
- **鉴权**：请求头 `Authorization: Bearer <token>`；登录、注册、商品查询、支付回调、接口文档等在白名单内免登录
- **逻辑删除**：统一使用 `isDeleted` 字段（1 删 / 0 正常），勿物理删除
- **表名**：实体类对应 MySQL 保留字表名时须用反引号转义
- **支付回调**：必须幂等，同一支付记录不可重复处理
