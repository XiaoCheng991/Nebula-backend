# 技术栈分析

**分析日期:** 2025-03-20

## 语言与运行时

**主要语言:** Java 21

- **Java版本:** Java 21 (LTS版本)
- **编译器:** OpenJDK 21
- **运行时:** JVM (Java Virtual Machine)

**主要技术栈:**

- **Spring Boot 3.2.2** - 应用框架
- **Spring Framework 6.x** - 核心框架
- **MyBatis Plus 3.6.x** - ORM框架
- **MySQL 8.x** - 数据库
- **Redis 7.x** - 分布式缓存
- **Elasticsearch 8.x** - 全文搜索
- **JWT** - JSON Web Token
- **SLF4J + Logback** - 日志框架

## 依赖管理

**构建工具:** Maven 3.x

**当前依赖版本:**

```xml
<!-- 主应用依赖 -->
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.2.2</version>
</parent>

<!-- 模块组成 -->
<modules>
    <module>nebula-common</module>
    <module>nebula-model</module>
    <module>nebula-config</module>
    <module>nebula-service</module>
    <module>nebula-api</module>
    <module>nebula-admin</module>
</modules>
```

## 配置文件

**配置文件结构:**

```
nebula-backend/
├── nebula-admin/src/main/resources/
│   ├── application.yml              # 主配置文件
│   ├── application-dev.yml          # 开发环境配置
│   ├── application-prod.yml         # 生产环境配置
│   └── .env                         # 环境变量配置
├── nebula-config/src/main/resources/
│   ├── application.yml              # 配置模块配置
│   └── mapper/                      # MyBatis XML映射文件
│       ├── blog/
│       └── system/
└── nebula-service/src/main/resources/
    └── mapper/                      # MyBatis XML映射文件
        ├── blog/
        └── system/
```

**配置类:**

- `SecurityConfig.java` - 安全配置
- `MybatisPlusConfig.java` - MyBatis Plus配置
- `RedisConfig.java` - Redis配置
- `SwaggerConfig.java` - API文档配置
- `SaTokenConfig.java` - 会话管理配置

## 测试框架

**测试依赖:**

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```

**测试目录结构:**

```
src/test/java/
├── com/nebula/api/controller/
├── com/nebula/service/
│   ├── service/
│   └── mapper/
└── com/nebula/common/
    └── util/
```

## 开发工具

**开发环境:**

- **IDE:** IntelliJ IDEA Ultimate
- **代码管理:** Git
- **包管理:** Maven Central + 华为云Maven镜像
- **API文档:** Swagger UI + OpenAPI 3
- **开发流程:** 基于测试的开发 (TDD)

## 开发流程

**开发流程:**

1. 分析需求与设计
2. 创建测试案例
3. 实现业务逻辑
4. 测试验证
5. 代码审查
6. 测试覆盖率验证

**代码质量标准:**

- 测试覆盖率 ≥ 80%
- 代码风格一致性
- 错误处理完善
- 安全性检查

---

*技术栈分析: 2025-03-20*