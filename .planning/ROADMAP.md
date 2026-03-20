# Roadmap: NebulaHub Backend

**Created:** 2026-03-20
**Requirements:** 25 v1 requirements mapped to 6 phases
**Status:** Planning complete

## 项目概览

NebulaHub 后端项目采用多模块 Maven 架构，基于 Spring Boot 3.x + Java 21，提供完整的认证、博客、系统管理功能。当前状态：博客笔记功能开发中，基础架构已搭建完成。

---

## 6 个开发阶段

| # | 阶段 | 目标 | 需求 | 成功标准 |
|---|------|------|------|----------|
| 1 | 认证与安全 | 建立安全的认证基础 | AUTH-01 ~ AUTH-05 | 用户可以注册、登录、使用GitHub登录，Token验证通过 |
| 2 | 用户管理 | 实现用户生命周期管理 | USER-01 ~ USER-05 | 管理员可以管理用户，用户可以管理个人资料 |
| 3 | 博客核心功能 | 实现博客系统基础功能 | BLOG-01 ~ BLOG-08 | 用户可以创建、编辑、删除文章，博客笔记功能完整 |
| 4 | 系统管理 | 建立权限控制与日志系统 | SYS-01 ~ SYS-06 | 管理员可以管理角色权限，操作日志记录完整 |
| 5 | 文件管理 | 实现文件上传下载功能 | FILE-01 ~ FILE-03 | 用户可以上传下载文件到MinIO，文件管理完整 |
| 6 | 即时通讯 | 实现实时消息系统 | IM-01 ~ IM-04 | 用户可以发送接收消息，敏感词过滤正常工作 |

---

## 阶段详情

### Phase 1: 认证与安全

**目标:** 建立安全的认证基础，支持多种登录方式
**需求:** AUTH-01 ~ AUTH-05
**成功标准:**
1. 用户可以使用邮箱密码注册账户
2. 用户可以使用GitHub OAuth登录
3. 用户可以重置密码
4. 用户登录后保持会话，Token验证通过
5. 用户可以登出

**关键实现:**
- Spring Security + JWT 认证配置
- GitHub OAuth 2.0 集成
- BCrypt 密码加密
- Token 刷新机制
- 登录日志记录
- 登录尝试限制

---

### Phase 2: 用户管理

**目标:** 实现用户生命周期管理，支持管理员操作
**需求:** USER-01 ~ USER-05
**成功标准:**
1. 管理员可以查看用户列表
2. 管理员可以修改用户状态（启用/禁用）
3. 管理员可以删除用户
4. 用户可以查看个人资料
5. 用户可以修改个人资料

**关键实现:**
- RBAC 权限模型
- 用户管理Controller/Service
- 数据验证与异常处理
- 个人资料管理
- 用户状态管理

---

### Phase 3: 博客核心功能

**目标:** 实现完整的博客系统，博客笔记功能完整
**需求:** BLOG-01 ~ BLOG-08
**成功标准:**
1. 用户可以创建博客文章
2. 用户可以编辑自己的文章
3. 用户可以删除自己的文章
4. 用户可以查看文章详情
5. 用户可以按分类查看文章
6. 用户可以搜索文章
7. 用户可以评论文章
8. 博客笔记功能完整实现

**关键实现:**
- BlogArticle CRUD 操作
- 博客笔记功能 (基于 BlogArticle 扩展)
- 文章分类与标签管理
- 文章评论系统
- 全文搜索 (Elasticsearch)
- 文件上传 (MinIO)

---

### Phase 4: 系统管理

**目标:** 建立权限控制与日志系统
**需求:** SYS-01 ~ SYS-06
**成功标准:**
1. 管理员可以管理角色
2. 管理员可以管理菜单
3. 管理员可以分配角色权限
4. 管理员可以管理字典数据
5. 管理员可以查看操作日志
6. 管理员可以管理在线用户

**关键实现:**
- 角色管理 (SysRole)
- 菜单管理 (SysMenu)
- 权限控制 (@RequirePermission)
- 字典管理 (SysDictType, SysDictItem)
- 操作日志 (SysOperationLog)
- 在线用户管理 (SysOnlineUser)
- 数据范围过滤 (@DataScope)
- 操作日志注解 (@OperationLog)

---

### Phase 5: 文件管理

**目标:** 实现文件上传下载功能
**需求:** FILE-01 ~ FILE-03
**成功标准:**
1. 用户可以上传文件到MinIO
2. 用户可以下载文件
3. 用户可以删除文件

**关键实现:**
- MinIO 配置与集成
- 文件上传Controller/Service
- 文件下载服务
- 文件删除功能
- 文件信息管理

---

### Phase 6: 即时通讯

**目标:** 实现实时消息系统
**需求:** IM-01 ~ IM-04
**成功标准:**
1. 用户可以发送消息
2. 用户可以接收消息
3. 敏感词过滤功能正常工作
4. 用户封禁功能正常工作

**关键实现:**
- WebSocket + STOMP 配置
- 消息归档 (ImMessageArchive)
- 敏感词过滤 (ImSensitiveWord)
- 用户封禁 (ImUserBan)
- 实时消息推送

---

## 需求追踪

| 需求 | 阶段 | 状态 | 完成度 |
|------|------|------|--------|
| AUTH-01 | 1 | Pending | 0% |
| AUTH-02 | 1 | Pending | 0% |
| AUTH-03 | 1 | Pending | 0% |
| AUTH-04 | 1 | Pending | 0% |
| AUTH-05 | 1 | Pending | 0% |
| USER-01 | 2 | Pending | 0% |
| USER-02 | 2 | Pending | 0% |
| USER-03 | 2 | Pending | 0% |
| USER-04 | 2 | Pending | 0% |
| USER-05 | 2 | Pending | 0% |
| BLOG-01 | 3 | Pending | 0% |
| BLOG-02 | 3 | Pending | 0% |
| BLOG-03 | 3 | Pending | 0% |
| BLOG-04 | 3 | Pending | 0% |
| BLOG-05 | 3 | Pending | 0% |
| BLOG-06 | 3 | Pending | 0% |
| BLOG-07 | 3 | Pending | 0% |
| BLOG-08 | 3 | Pending | 0% |
| SYS-01 | 4 | Pending | 0% |
| SYS-02 | 4 | Pending | 0% |
| SYS-03 | 4 | Pending | 0% |
| SYS-04 | 4 | Pending | 0% |
| SYS-05 | 4 | Pending | 0% |
| SYS-06 | 4 | Pending | 0% |
| FILE-01 | 5 | Pending | 0% |
| FILE-02 | 5 | Pending | 0% |
| FILE-03 | 5 | Pending | 0% |
| IM-01 | 6 | Pending | 0% |
| IM-02 | 6 | Pending | 0% |
| IM-03 | 6 | Pending | 0% |
| IM-04 | 6 | Pending | 0% |

**覆盖率:**
- v1 需求: 25 个
- 已分配到阶段: 25 个
- 未分配: 0 个 ✓

---

## 项目状态

### 当前进度
- **项目类型**: 绿色田野项目 (Greenfield)
- **技术栈**: Spring Boot 3.2.2 + Java 21
- **架构**: 多模块 Maven 架构
- **状态**: 基础架构已搭建，博客笔记功能开发中

### 关键约束
- **技术栈**: Java 21, Spring Boot 3.x, Maven
- **数据库**: MySQL 8.x
- **缓存**: Redis 7.x
- **安全**: JWT + Spring Security
- **文件存储**: MinIO
- **搜索**: Elasticsearch

### 开发建议
1. 统一使用 Java 21 特性
2. 遵循现有项目分层规范
3. 使用 MyBatis Plus 的代码生成器
4. 确保所有 DTO/VO 的完整性验证
5. 使用全局异常处理器统一异常处理

---

## 相关文档

- [.planning/PROJECT.md](.planning/PROJECT.md) - 项目概述
- [.planning/REQUIREMENTS.md](.planning/REQUIREMENTS.md) - 需求定义
- [.planning/config.json](.planning/config.json) - 工作流配置
- [.planning/research/SUMMARY.md](.planning/research/SUMMARY.md) - 研究摘要
- [.planning/codebase/STACK.md](.planning/codebase/STACK.md) - 技术栈分析
- [.planning/codebase/ARCHITECTURE.md](.planning/codebase/ARCHITECTURE.md) - 架构分析
- [.planning/codebase/STRUCTURE.md](.planning/codebase/STRUCTURE.md) - 目录结构
- [.planning/codebase/CONVENTIONS.md](.planning/codebase/CONVENTIONS.md) - 编码规范
- [.planning/codebase/TESTING.md](.planning/codebase/TESTING.md) - 测试模式
- [.planning/codebase/INTEGRATIONS.md](.planning/codebase/INTEGRATIONS.md) - 外部集成
- [.planning/codebase/CONCERNS.md](.planning/codebase/CONCERNS.md) - 关注点分析

---

## 下一步

**准备开始执行:**

1. **Phase 1: 认证与安全** - 建立安全的认证基础
2. **讨论阶段 1** - 收集上下文和澄清方法
3. **规划阶段 1** - 创建详细的实现计划

**可用命令:**
- `/gsd:discuss-phase 1` - 讨论阶段 1
- `/gsd:plan-phase 1` - 直接规划阶段 1
- `/gsd:execute-phase 1` - 执行阶段 1

---

*ROADMAP.md 生成于 2026-03-20*
*需求映射完成: 25/25 个需求已分配*
*阶段结构: 6 个开发阶段*