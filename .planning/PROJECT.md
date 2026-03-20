# Project: NebulaHub Backend

**Type:** Brownfield (Existing Codebase)
**Created:** 2026-03-20
**Last Updated:** 2026-03-20

## Overview

NebulaHub 是一个现代化的博客平台后端服务，采用 Spring Boot 3.2.2 + Java 21 技术栈，提供用户管理、认证授权、博客系统、系统管理、文件管理和即时通讯等核心功能。

## Project Context

### Current Status

- **项目类型**: 棕色田野项目 (Brownfield) - 已有代码基础
- **开发状态**: 基础架构已搭建，部分功能开发中
- **Git 分支**: main
- **当前进度**: 博客笔记功能开发中

### Tech Stack

| Component | Technology | Version |
|-----------|------------|---------|
| Java | OpenJDK | 21 |
| Framework | Spring Boot | 3.2.2 |
| Cloud | Spring Cloud | 2023.0.0 |
| ORM | MyBatis Plus | 3.5.5 |
| Database | PostgreSQL | 14+ |
| Cache | Redis | 7.x |
| Security | Sa-Token | Latest |
| OAuth | GitHub OAuth 2.0 | - |
| File Storage | MinIO | Latest |
| Search | Elasticsearch | 8.x |
| JSON | FastJSON2 | 2.0.47 |
| Utils | Hutool | 5.8.25 |
| API Doc | Knife4j | 4.5.0 |
| Build Tool | Maven | 3.8+ |

### Module Structure

```
Nebula-backend/
├── nebula-common/      # 公共模块：注解、异常、工具类
├── nebula-model/       # 数据模型：实体类、DTO、VO、枚举
├── nebula-config/      # 配置模块：安全配置、Redis 配置、异常处理
├── nebula-service/     # 服务模块：Service 实现、Mapper、业务逻辑
├── nebula-api/         # API 模块：Controller、公共 API
└── nebula-admin/       # 管理模块：启动类、配置文件
```

## Core Domains

### 1. Authentication (认证)
- 邮箱密码注册/登录
- GitHub OAuth 2.0 第三方登录
- 密码重置功能
- 邮箱验证
- JWT Token 管理
- 登录日志记录
- 登录尝试限制

### 2. User Management (用户管理)
- 用户 CRUD 操作
- 用户状态管理
- 个人资料管理
- 用户搜索
- RBAC 权限模型

### 3. Blog System (博客系统)
- 博客文章管理 (BlogArticle)
- 博客笔记功能 (BlogNote)
- 分类管理 (BlogCategory)
- 标签管理 (BlogTag)
- 评论系统 (BlogComment)
- 文章搜索 (Elasticsearch)

### 4. System Management (系统管理)
- 角色管理 (SysRole)
- 菜单管理 (SysMenu)
- 用户角色分配 (SysUserRole)
- 角色菜单权限 (SysRoleMenu)
- 字典管理 (SysDictType, SysDictItem)
- 操作日志 (SysOperationLog)
- 在线用户管理 (SysOnlineUser)

### 5. File Management (文件管理)
- 文件上传 (MinIO)
- 文件下载
- 文件删除
- 文件信息管理

### 6. Instant Messaging (即时通讯)
- 消息发送/接收
- 消息归档 (ImMessageArchive)
- 敏感词过滤 (ImSensitiveWord)
- 用户封禁 (ImUserBan)

## Key Features

### Security Features
- Sa-Token 认证框架
- BCrypt 密码加密
- GitHub OAuth 2.0 集成
- 登录尝试限制
- 邮箱验证机制
- 密码重置流程
- 权限控制注解 (@RequirePermission)
- 数据范围过滤 (@DataScope)

### Developer Experience
- Knife4j API 文档
- 全局异常处理
- 统一结果封装 (Result<T>)
- MyBatis Plus 代码生成器支持
- Lombok 简化代码
- Hutool 工具类库

## Database Schema

### Core Tables
- `sys_user` / `sys_users` - 用户表
- `sys_role` - 角色表
- `sys_menu` - 菜单表
- `sys_user_role` - 用户角色关联
- `sys_role_menu` - 角色菜单关联
- `sys_dict_type` - 字典类型
- `sys_dict_item` - 字典项
- `sys_operation_log` - 操作日志
- `sys_online_user` - 在线用户

### Blog Tables
- `blog` - 博客表
- `blog_article` - 博客文章表
- `blog_note` - 博客笔记表
- `blog_category` - 分类表
- `blog_tag` - 标签表
- `blog_article_tag` - 文章标签关联
- `blog_comment` - 评论表

### Auth Tables
- `email_verification` - 邮箱验证
- `login_attempt` - 登录尝试
- `login_log` - 登录日志
- `password_reset_token` - 密码重置令牌

### IM Tables
- `im_message_archive` - 消息归档
- `im_sensitive_word` - 敏感词
- `im_user_ban` - 用户封禁

## API Endpoints

### Authentication
- `POST /api/auth/login` - 用户登录
- `POST /api/auth/register` - 用户注册
- `POST /api/auth/logout` - 用户登出
- `GET /api/auth/user-info` - 获取用户信息

### OAuth
- `GET /api/oauth/github/authorize` - 获取 GitHub 授权 URL
- `GET /api/oauth/github/callback` - GitHub OAuth 回调
- `GET /api/oauth/github/info` - 获取 GitHub 用户信息
- `POST /api/oauth/github/confirm` - 确认 GitHub 登录

### User
- `GET /api/users` - 查询所有用户
- `GET /api/users/{id}` - 根据 ID 查询用户
- `POST /api/users` - 创建用户
- `PUT /api/users` - 更新用户
- `DELETE /api/users/{id}` - 删除用户

### Blog Note
- `POST /api/blog/note` - 创建碎碎念
- `PUT /api/blog/note` - 更新碎碎念
- `GET /api/blog/note/{id}` - 获取碎碎念详情
- `GET /api/blog/note/my` - 获取我的碎碎念
- `GET /api/blog/note/public` - 获取公开碎碎念
- `DELETE /api/blog/note/{id}` - 删除碎碎念

### File
- `POST /api/file/upload` - 文件上传
- `GET /api/file/download/{id}` - 文件下载
- `DELETE /api/file/{id}` - 文件删除

## Environment Variables

```bash
# Database
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/postgres
SPRING_DATASOURCE_USERNAME=admin
SPRING_DATASOURCE_PASSWORD=admin

# Redis
SPRING_DATA_REDIS_HOST=localhost
SPRING_DATA_REDIS_PORT=6379
SPRING_DATA_REDIS_PASSWORD=123123

# GitHub OAuth
GITHUB_CLIENT_ID=your_client_id
GITHUB_CLIENT_SECRET=your_client_secret
GITHUB_REDIRECT_URI=http://localhost:8080/api/oauth/github/callback
FRONTEND_CALLBACK_URL=http://localhost:3000/auth/github/callback

# MinIO
MINIO_ENDPOINT=http://localhost:9000
MINIO_ACCESS_KEY=minioadmin
MINIO_SECRET_KEY=minioadmin
```

## Development Guidelines

### Code Conventions
- 统一使用 Java 21 特性
- 遵循现有项目分层规范 (Controller -> Service -> Mapper)
- 使用 Lombok 简化实体类
- 使用 Hutool 工具类
- 统一异常处理 (GlobalExceptionHandler)
- 统一结果封装 (Result<T>)

### Git Workflow
- 使用 GSD 工作流进行开发
- 每个阶段完成后提交
- 遵循 commit message 规范

## References

- [.planning/config.json](config.json) - GSD 工作流配置
- [.planning/REQUIREMENTS.md](REQUIREMENTS.md) - 需求定义
- [.planning/ROADMAP.md](ROADMAP.md) - 开发路线图
- [.planning/codebase/](codebase/) - 代码库分析文档

---

*PROJECT.md - NebulaHub Backend Project Context*
*Last updated: 2026-03-20*
