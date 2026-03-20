# NebulaHub Backend Project

**项目类型:** 绿色田野项目 (Greenfield)
**创建日期:** 2026-03-20
**最后更新:** 2026-03-20

---

## 项目概述

NebulaHub 是一个现代化的全栈协作平台后端服务，集成了用户认证、博客管理、即时通讯、系统管理等多个功能模块。项目采用 Spring Boot 3.x + Java 21 技术栈，基于多模块 Maven 架构设计。

### 核心功能模块

1. **认证授权模块** - 支持用户名密码登录、GitHub OAuth 第三方登录、JWT 令牌管理
2. **博客文章模块** - 完整的博客系统，支持文章管理、分类、标签
3. **日常碎碎念模块** - 基于博客系统的轻量级随笔记录功能，支持心情、位置、天气等元数据
4. **即时通讯模块** - WebSocket 实时消息传递、消息归档、敏感词过滤
5. **系统管理模块** - 用户管理、角色管理、菜单管理、权限控制、操作日志

---

## 技术栈

### 核心运行时
- **Java 21** (LTS 版本)
- **Spring Boot 3.2.2**
- **Spring Framework 6.x**
- **Spring Cloud 2023.0.0**

### 数据存储
- **MySQL 8.x** - 主数据库
- **Redis 7.x** - 缓存与会话管理
- **Elasticsearch 8.x** - 全文搜索

### ORM 与数据访问
- **MyBatis Plus 3.5.5** - ORM 框架
- **HikariCP** - 数据库连接池

### 安全与认证
- **Spring Security** - 安全框架
- **JWT (io.jsonwebtoken 0.12.3)** - Token 认证
- **BCrypt** - 密码加密

### 工具与依赖
- **Hutool 5.8.25** - Java 工具类库
- **FastJSON2 2.0.47** - JSON 处理
- **Lombok** - 代码简化
- **SLF4J + Logback** - 日志框架

### API 文档
- **Knife4j 4.5.0** - OpenAPI 3 文档

---

## 项目结构

```
Nebula-backend/
├── nebula-admin/          # 应用入口模块
│   └── NebulaHubBackendApplication.java
├── nebula-api/            # API 控制器层
│   └── controller/        # REST 控制器
│       ├── AuthController.java
│       ├── BlogNoteController.java
│       ├── UserController.java
│       ├── ProfileController.java
│       └── ...
├── nebula-config/         # 配置模块
│   └── config/            # 配置类
│       ├── SecurityConfig.java
│       ├── MybatisPlusConfig.java
│       ├── RedisConfig.java
│       └── ...
├── nebula-common/         # 公共工具模块
│   ├── annotation/        # 自定义注解
│   ├── constant/          # 常量定义
│   ├── exception/         # 异常处理
│   ├── util/              # 工具类
│   └── validator/         # 验证器
├── nebula-model/          # 数据模型模块
│   ├── dto/               # 数据传输对象
│   ├── entity/            # 数据库实体
│   │   ├── system/        # 系统管理实体
│   │   ├── blog/          # 博客相关实体
│   │   ├── im/            # 即时通讯实体
│   │   └── ...
│   ├── enums/             # 枚举类型
│   └── vo/                # 值对象
├── nebula-service/        # 业务逻辑模块
│   ├── mapper/            # MyBatis 映射接口
│   ├── service/           # 服务接口
│   │   └── impl/          # 服务实现
│   └── resources/mapper/  # MyBatis XML 映射
├── pom.xml                # 父 POM
├── db/                    # 数据库迁移脚本
│   ├── assign_super_admin.sql
│   ├── init_admin_data.sql
│   ├── migration_blog_note.sql
│   └── ...
├── .env.example           # 环境变量示例
└── .planning/codebase/    # 代码库文档
    ├── STACK.md
    ├── ARCHITECTURE.md
    ├── STRUCTURE.md
    ├── CONVENTIONS.md
    ├── TESTING.md
    ├── INTEGRATIONS.md
    └── CONCERNS.md
```

---

## 数据库架构

### 系统管理表 (sys_*)
- `sys_user` - 用户表
- `sys_role` - 角色表
- `sys_role_user` - 角色 - 用户关联
- `sys_role_menu` - 角色 - 菜单关联
- `sys_menu` - 菜单表
- `sys_dict_type` - 字典类型
- `sys_dict_item` - 字典项
- `sys_operation_log` - 操作日志
- `sys_online_user` - 在线用户

### 博客相关表 (blog_*)
- `blog_article` - 博客文章表
- `BlogNote` - 日常碎碎念 (基于 blog_article 表扩展)

### 即时通讯表 (im_*)
- `im_message_archive` - 消息归档
- `im_sensitive_word` - 敏感词
- `im_user_ban` - 用户禁言

### 认证相关表
- `user` - 用户表 (简化版)
- `email_verification` - 邮箱验证
- `login_attempt` - 登录尝试记录
- `login_log` - 登录日志
- `password_reset_token` - 密码重置令牌

---

## API 接口概览

### 认证相关
- `POST /api/auth/register` - 用户注册
- `POST /api/auth/login` - 用户登录
- `POST /api/auth/logout` - 用户登出
- `POST /api/auth/refresh` - 刷新 Token
- `POST /api/oauth/github/callback` - GitHub OAuth 回调

### 博客碎碎念
- `POST /api/blog/note` - 创建碎碎念
- `PUT /api/blog/note` - 更新碎碎念
- `DELETE /api/blog/note/{id}` - 删除碎碎念
- `GET /api/blog/note/my` - 获取我的碎碎念
- `GET /api/blog/note/public` - 获取公开碎碎念
- `GET /api/blog/note/{id}` - 获取单条碎碎念

### 用户管理
- `GET /api/users` - 获取用户列表
- `GET /api/users/{id}` - 获取用户详情
- `PUT /api/users` - 更新用户信息

### 文件管理
- `POST /api/file/upload` - 文件上传
- `GET /api/file/{id}` - 获取文件信息

---

## 编码规范

### 命名约定
- **实体类**: PascalCase (e.g., `BlogArticle.java`)
- **DTO**: PascalCase + `DTO` (e.g., `BlogNoteCreateDTO.java`)
- **VO**: PascalCase + `VO` (e.g., `BlogArticleVO.java`)
- **Service**: PascalCase + `Service` (e.g., `UserService.java`)
- **ServiceImpl**: PascalCase + `ServiceImpl` (e.g., `UserServiceImpl.java`)
- **Mapper**: PascalCase + `Mapper` (e.g., `UserMapper.java`)
- **Controller**: PascalCase + `Controller` (e.g., `BlogNoteController.java`)

### 包命名
```
com.nebula.{module}.{submodule}
```
示例:
- `com.nebula.api.controller` - API 控制器
- `com.nebula.model.entity` - 数据库实体
- `com.nebula.service.impl` - 服务实现
- `com.nebula.common.util` - 公共工具

### 错误处理
- 使用 `BusinessException` 统一异常处理
- `GlobalExceptionHandler` 集中处理异常
- 错误代码规范:
  - 1xxx: 通用错误
  - 2xxx: 认证错误
  - 3xxx: 注册错误
  - 4xxx: 用户相关错误
  - 9xxx: 业务逻辑错误

### 日志规范
- 使用 SLF4J + Lombok `@Slf4j`
- 结构化日志，使用占位符
- 敏感数据脱敏 (Token、Email 等)

---

## 配置说明

### 环境变量 (.env)
```bash
# GitHub OAuth
GITHUB_CLIENT_ID=xxx
GITHUB_CLIENT_SECRET=xxx

# 数据库
DB_HOST=localhost
DB_PORT=5432
DB_NAME=nebula
DB_USER=admin
DB_PASSWORD=admin

# Redis
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=xxx

# JWT
JWT_SECRET=xxx
JWT_ACCESS_TOKEN_EXPIRE=1800000
JWT_REFRESH_TOKEN_EXPIRE=604800000

# MinIO (文件存储)
MINIO_ENDPOINT=http://localhost:9000
MINIO_ACCESS_KEY=minioadmin
MINIO_SECRET_KEY=minioadmin
```

---

## 开发指南

### 环境要求
- JDK 21+
- Maven 3.6+
- MySQL 8.x
- Redis 7.x

### 启动项目
```bash
# 安装依赖
mvn clean install

# 运行项目
cd nebula-admin
mvn spring-boot:run

# 或使用
./mvnw spring-boot:run
```

### API 文档访问
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI 文档：`http://localhost:8080/v3/api-docs`

---

## 测试策略

### 当前状态
- 测试框架已配置 (spring-boot-starter-test)
- 测试目录结构已建立
- **待实现**: 单元测试、集成测试

### 推荐测试框架
- **JUnit 5** - 测试框架
- **Mockito** - Mock 框架
- **Spring Boot Test** - 集成测试

### 测试覆盖率目标
- 业务逻辑：≥ 80%
- 工具类：100%
- 集成层：≥ 70%

---

## 技术债务与改进

### 高优先级
- [ ] 建立完整的测试框架
- [ ] 配置代码质量工具 (Checkstyle, PMD)
- [ ] 完善安全配置

### 中优先级
- [ ] 添加应用监控 (Actuator)
- [ ] 优化数据库查询与索引
- [ ] 配置缓存策略

### 低优先级
- [ ] 完善部署文档
- [ ] 优化 CI/CD 流程
- [ ] 性能分析工具集成

---

## 外部集成

### 数据库
- MySQL 8.x 通过 HikariCP 连接
- 表名使用 snake_case 命名

### 缓存
- Redis 7.x 用于会话与热点数据
- Lettuce 连接池

### 第三方服务
- **GitHub OAuth** - 第三方登录
- **MinIO** - 文件存储
- **邮件服务 (SMTP)** - 邮件验证

---

## 安全考虑

### 认证安全
- JWT Token 认证
- BCrypt 密码加密
- Token 刷新机制

### 数据保护
- 敏感数据脱敏 (日志)
- 输入验证 (Bean Validation)
- CSRF 保护

### 权限控制
- RBAC 权限模型
- 方法级权限注解 `@RequirePermission`
- 数据范围过滤 `@DataScope`

---

## 相关文档

- [.planning/codebase/STACK.md](.planning/codebase/STACK.md) - 技术栈分析
- [.planning/codebase/ARCHITECTURE.md](.planning/codebase/ARCHITECTURE.md) - 架构分析
- [.planning/codebase/STRUCTURE.md](.planning/codebase/STRUCTURE.md) - 目录结构
- [.planning/codebase/CONVENTIONS.md](.planning/codebase/CONVENTIONS.md) - 编码规范
- [.planning/codebase/TESTING.md](.planning/codebase/TESTING.md) - 测试模式
- [.planning/codebase/INTEGRATIONS.md](.planning/codebase/INTEGRATIONS.md) - 外部集成
- [.planning/codebase/CONCERNS.md](.planning/codebase/CONCERNS.md) - 关注点分析
- [BLOG_NOTE_USAGE.md](BLOG_NOTE_USAGE.md) - 碎碎念功能使用指南

---

## 版本历史

| 版本 | 日期 | 说明 |
|------|------|------|
| 0.0.1-SNAPSHOT | 2026-03-20 | 初始版本 |

---

*PROJECT.md 生成于 2026-03-20*
