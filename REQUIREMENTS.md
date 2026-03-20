# Requirements: NebulaHub Backend

**Defined:** 2026-03-20
**Last Updated:** 2026-03-20
**Core Value:** 提供现代化、安全的后端服务，支持用户管理、博客系统、即时通讯和系统管理功能

---

## v1 Requirements

Requirements for initial release. Each maps to roadmap phases.

### Authentication & Authorization

用户认证与授权是系统的核心基础，确保安全性和用户体验。

- [x] **AUTH-01**: 用户可以使用用户名密码注册账户
- [x] **AUTH-02**: 用户可以登录账户
- [x] **AUTH-03**: 用户可以登出账户
- [x] **AUTH-04**: 用户可以刷新 Token 保持会话
- [x] **AUTH-05**: 用户可以使用 GitHub OAuth 第三方登录
- [x] **AUTH-06**: 系统支持 JWT Token 认证机制
- [x] **AUTH-07**: 系统支持 BCrypt 密码加密
- [x] **AUTH-08**: 系统支持 Token 黑名单机制
- [ ] **AUTH-09**: 用户可以重置密码
- [ ] **AUTH-10**: 用户可以绑定/解绑第三方账号
- [ ] **AUTH-11**: 支持双因素认证 (2FA)
- [ ] **AUTH-12**: 登录尝试限制与账户锁定机制
- [ ] **AUTH-13**: 登录风险评估与异常检测

### User Management

用户管理系统提供用户信息的 CRUD 操作和用户详情查询。

- [x] **USER-01**: 用户可以使用昵称 (nickname) 作为显示名称
- [x] **USER-02**: 用户可以查看个人资料
- [x] **USER-03**: 用户可以修改个人资料 (头像、简介、昵称等)
- [x] **USER-04**: 用户可以设置在线状态
- [x] **USER-05**: 用户可以上传和管理头像
- [ ] **USER-06**: 管理员可以查看用户列表
- [ ] **USER-07**: 管理员可以修改用户状态 (激活/禁用)
- [ ] **USER-08**: 管理员可以删除用户
- [ ] **USER-09**: 用户可以搜索其他用户
- [ ] **USER-10**: 用户列表支持分页和筛选

### Blog System

博客系统支持完整的文章管理和日常碎碎念功能。

#### Blog Articles (正式文章)

- [x] **BLOG-01**: 用户可以创建博客文章
- [x] **BLOG-02**: 用户可以编辑自己的文章
- [x] **BLOG-03**: 用户可以删除自己的文章
- [x] **BLOG-04**: 用户可以查看文章详情
- [x] **BLOG-05**: 用户可以按分类查看文章
- [x] **BLOG-06**: 用户可以按标签筛选文章
- [x] **BLOG-07**: 用户可以搜索文章 (标题、内容)
- [x] **BLOG-08**: 文章支持 Markdown 格式
- [ ] **BLOG-09**: 文章支持草稿功能
- [ ] **BLOG-10**: 文章支持定时发布
- [ ] **BLOG-11**: 文章支持版本历史
- [ ] **BLOG-12**: 文章支持阅读统计

#### Blog Notes (日常碎碎念)

- [x] **BLOG-13**: 用户可以创建日常碎碎念 (基于 blog_article 表)
- [x] **BLOG-14**: 碎碎念支持心情记录 (mood)
- [x] **BLOG-15**: 碎碎念支持位置记录 (location)
- [x] **BLOG-16**: 碎碎念支持天气记录 (weather)
- [x] **BLOG-17**: 碎碎念支持公开/私有控制
- [x] **BLOG-18**: 碎碎念支持标签分类
- [x] **BLOG-19**: 用户可以查看我的碎碎念列表
- [x] **BLOG-20**: 用户可以查看公开碎碎念列表
- [x] **BLOG-21**: 碎碎念支持按心情筛选
- [x] **BLOG-22**: 碎碎念支持按标签筛选
- [x] **BLOG-23**: 碎碎念支持按时间倒序展示
- [ ] **BLOG-24**: 碎碎念支持点赞功能
- [ ] **BLOG-25**: 碎碎念支持评论功能
- [ ] **BLOG-26**: 碎碎念支持心情统计分析
- [ ] **BLOG-27**: 碎碎念支持位置地图展示

#### Blog Infrastructure (博客基础设施)

- [x] **BLOG-28**: 系统支持分类管理 (BlogCategory)
- [x] **BLOG-29**: 系统支持标签管理 (BlogTag)
- [x] **BLOG-30**: 系统支持文章 - 标签关联 (BlogArticleTag)
- [ ] **BLOG-31**: 系统支持文章评论 (BlogComment)
- [ ] **BLOG-32**: 系统支持文章点赞
- [ ] **BLOG-33**: 系统支持文章收藏

### System Management

系统管理模块提供基于 RBAC 的权限管理和系统配置。

#### Role Management

- [x] **SYS-01**: 管理员可以创建角色
- [x] **SYS-02**: 管理员可以编辑角色
- [x] **SYS-03**: 管理员可以删除角色
- [x] **SYS-04**: 管理员可以分配角色给用户
- [x] **SYS-05**: 系统支持数据范围过滤 (@DataScope)

#### Menu Management

- [x] **SYS-06**: 管理员可以创建菜单
- [x] **SYS-07**: 管理员可以编辑菜单
- [x] **SYS-08**: 管理员可以删除菜单
- [x] **SYS-09**: 菜单支持树形结构
- [x] **SYS-10**: 管理员可以为用户分配菜单权限
- [x] **SYS-11**: 用户登录后获取动态菜单

#### Permission Management

- [x] **SYS-12**: 系统支持基于注解的权限控制 (@RequirePermission)
- [x] **SYS-13**: 角色 - 菜单关联管理 (SysRoleMenu)
- [x] **SYS-14**: 用户 - 角色关联管理 (SysUserRole)

#### Dictionary Management

- [x] **SYS-15**: 管理员可以创建字典类型 (SysDictType)
- [x] **SYS-16**: 管理员可以管理字典项 (SysDictItem)
- [x] **SYS-17**: 字典支持排序和状态管理

#### Operation Log

- [x] **SYS-18**: 系统记录操作日志 (SysOperationLog)
- [x] **SYS-19**: 支持通过注解自动记录操作 (@OperationLog)
- [ ] **SYS-20**: 管理员可以查看操作日志列表
- [ ] **SYS-21**: 操作日志支持搜索和筛选

#### Online User Management

- [x] **SYS-22**: 系统记录在线用户 (SysOnlineUser)
- [ ] **SYS-23**: 管理员可以查看在线用户列表
- [ ] **SYS-24**: 管理员可以强制下线用户

### File Management

文件管理支持 MinIO 对象存储集成。

- [x] **FILE-01**: 用户可以上传文件到 MinIO
- [x] **FILE-02**: 用户可以下载文件
- [x] **FILE-03**: 用户可以删除文件
- [x] **FILE-04**: 系统支持头像上传
- [ ] **FILE-05**: 系统支持文件类型验证
- [ ] **FILE-06**: 系统支持文件大小限制
- [ ] **FILE-07**: 系统支持图片压缩和裁剪

### IM (Instant Messaging)

即时通讯模块支持实时消息传递和消息管理。

- [ ] **IM-01**: 用户可以发送消息
- [ ] **IM-02**: 用户可以接收消息
- [ ] **IM-03**: 系统支持 WebSocket 实时通信
- [ ] **IM-04**: 系统支持消息归档 (ImMessageArchive)
- [ ] **IM-05**: 系统支持敏感词过滤 (ImSensitiveWord)
- [ ] **IM-06**: 系统支持用户禁言 (ImUserBan)
- [ ] **IM-07**: 消息支持已读/未读状态
- [ ] **IM-08**: 支持群聊功能
- [ ] **IM-09**: 支持私聊功能

### Search

搜索功能支持全文检索和快速查询。

- [ ] **SEARCH-01**: 支持用户搜索 (Elasticsearch)
- [ ] **SEARCH-02**: 支持文章搜索
- [ ] **SEARCH-03**: 支持标签搜索
- [ ] **SEARCH-04**: 搜索结果支持分页

### Cache & Performance

缓存与性能优化确保系统响应速度。

- [x] **PERF-01**: 系统支持 Redis 缓存
- [x] **PERF-02**: 系统使用 HikariCP 连接池
- [ ] **PERF-03**: 热点数据缓存策略
- [ ] **PERF-04**: 数据库查询优化
- [ ] **PERF-05**: 接口响应时间监控

---

## v2 Requirements

Deferred to future releases. Tracked for future planning.

### Advanced Social Features

- **SOCIAL-01**: 用户关注系统
- **SOCIAL-02**: 粉丝管理
- **SOCIAL-03**: 用户互相关联
- **SOCIAL-04**: 动态时间线
- **SOCIAL-05**: 内容推荐算法

### Notification System

- **NOTIF-01**: 系统通知
- **NOTIF-02**: 邮件通知
- **NOTIF-03**: WebSocket 实时通知
- **NOTIF-04**: 通知偏好设置
- **NOTIF-05**: 通知历史

### Analytics & Statistics

- **STATS-01**: 用户活跃度统计
- **STATS-02**: 文章阅读统计
- **STATS-03**: 碎碎念心情统计
- **STATS-04**: 系统使用统计
- **STATS-05**: 数据可视化仪表板

### Content Moderation

- **MOD-01**: 内容审核机制
- **MOD-02**: 敏感内容自动检测
- **MOD-03**: 用户举报系统
- **MOD-04**: 内容回收站
- **MOD-05**: 审核日志

### API Enhancements

- **API-01**: API 版本控制
- **API-02**: API 限流
- **API-03**: API 使用统计
- **API-04**: GraphQL 支持
- **API-05**: Webhook 支持

---

## Non-Functional Requirements

### Security

- **SEC-01**: 所有密码使用 BCrypt 加密
- **SEC-02**: JWT Token 加密存储
- **SEC-03**: 敏感数据日志脱敏
- **SEC-04**: 输入参数验证
- **SEC-05**: CSRF 保护
- **SEC-06**: CORS 配置
- **SEC-07**: SQL 注入防护
- **SEC-08**: XSS 防护

### Performance

- **PERF-NT-01**: API 响应时间 < 200ms (95 分位)
- **PERF-NT-02**: 支持并发用户 >= 1000
- **PERF-NT-03**: 数据库查询优化
- **PERF-NT-04**: 缓存命中率 >= 80%

### Reliability

- **REL-01**: 系统可用性 >= 99.9%
- **REL-02**: 数据备份策略
- **REL-03**: 异常日志记录
- **REL-04**: 优雅降级机制

### Scalability

- **SCALE-01**: 水平扩展支持
- **SCALE-02**: 微服务架构预留
- **SCALE-03**: 消息队列支持
- **SCALE-04**: 分布式缓存支持

### Maintainability

- **MAINT-01**: 代码覆盖率 >= 80%
- **MAINT-02**: API 文档完整
- **MAINT-03**: 单元测试完善
- **MAINT-04**: 集成测试完善
- **MAINT-05**: 代码规范统一

### Observability

- **OBS-01**: 日志集中管理
- **OBS-02**: 指标监控
- **OBS-03**: 链路追踪
- **OBS-04**: 告警机制

---

## Out of Scope

Explicitly excluded features to prevent scope creep.

| Feature | Reason |
|---------|--------|
| 移动应用 (iOS/Android) | 当前版本专注于 Web 后端服务 |
| 实时视频通话 | 复杂度过高，非核心需求 |
| 区块链集成 | 当前架构不支持，未来版本考虑 |
| 人工智能推荐 | 需要大量数据，当前版本不包含 |
| 支付系统 | 非核心业务需求 |
| 多语言国际化 | 当前版本仅支持中文 |

---

## Technical Constraints

| Constraint | Description |
|------------|-------------|
| Java Version | 必须使用 Java 21 LTS |
| Spring Boot | 必须使用 3.x 版本 (Java 21 兼容) |
| Database | MySQL 8.x 或兼容版本 |
| Cache | Redis 7.x 或兼容版本 |
| Build Tool | Maven 3.6+ |
| Code Style | 遵循项目编码规范 (CONVENTIONS.md) |

---

## Traceability Matrix

Requirements to Phase mapping for roadmap planning.

| Requirement ID | Category | Phase | Priority | Status |
|----------------|----------|-------|----------|--------|
| AUTH-01 to AUTH-08 | Authentication | Phase 1 | High | Implemented |
| AUTH-09 to AUTH-13 | Authentication | Phase 2 | Medium | Pending |
| USER-01 to USER-05 | User | Phase 1 | High | Implemented |
| USER-06 to USER-10 | User | Phase 2 | Medium | Pending |
| BLOG-01 to BLOG-08 | Blog Article | Phase 3 | High | Implemented |
| BLOG-13 to BLOG-23 | Blog Note | Phase 3 | High | Implemented |
| BLOG-09 to BLOG-12 | Blog Article | Phase 3 | Medium | Pending |
| BLOG-24 to BLOG-33 | Blog Infra | Phase 3 | Low | Pending |
| SYS-01 to SYS-24 | System | Phase 4 | High | Partial |
| FILE-01 to FILE-07 | File | Phase 5 | Medium | Partial |
| IM-01 to IM-09 | IM | Phase 6 | Medium | Pending |
| SEARCH-01 to SEARCH-04 | Search | Phase 6 | Medium | Pending |
| PERF-01 to PERF-05 | Performance | Phase 7 | High | Partial |

---

## Appendix

### Glossary

| Term | Definition |
|------|------------|
| RBAC | Role-Based Access Control (基于角色的访问控制) |
| JWT | JSON Web Token (令牌认证) |
| DTO | Data Transfer Object (数据传输对象) |
| VO | Value Object (值对象) |
| MVP | Minimum Viable Product (最小可行产品) |

### Related Documents

- [PROJECT.md](PROJECT.md) - 项目概述文档
- [.planning/codebase/STACK.md](.planning/codebase/STACK.md) - 技术栈分析
- [.planning/codebase/ARCHITECTURE.md](.planning/codebase/ARCHITECTURE.md) - 架构分析
- [.planning/codebase/STRUCTURE.md](.planning/codebase/STRUCTURE.md) - 目录结构
- [.planning/codebase/CONVENTIONS.md](.planning/codebase/CONVENTIONS.md) - 编码规范
- [.planning/codebase/TESTING.md](.planning/codebase/TESTING.md) - 测试规范
- [BLOG_NOTE_USAGE.md](BLOG_NOTE_USAGE.md) - 碎碎念功能使用指南

---

*Requirements Document*
*Generated: 2026-03-20*
*Last Updated: 2026-03-20*
