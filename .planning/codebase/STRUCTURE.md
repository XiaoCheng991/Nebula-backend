# 目录结构分析

**分析日期:** 2025-03-20

## 根目录结构

```
nebula-backend/
├── nebula-admin/          # 应用入口点
├── nebula-api/            # 控制器层
├── nebula-config/         # 配置层
├── nebula-common/         # 公共工具
├── nebula-model/          # 数据模型
├── nebula-service/        # 业务逻辑
├── pom.xml               # 项目配置
└── README.md             # 项目说明
```

## 应用层 (nebula-admin)

```
nebula-admin/
├── src/main/java/
│   └── com/nebula/admin/
│       ├── NebulaHubBackendApplication.java  # 启动类
│       ├── controller/                       # 控制器
│       ├── service/                          # 服务
│       └── config/                           # 配置
└── src/main/resources/
    ├── application.yml                        # 配置文件
    └── static/                                # 静态资源
```

## 接口层 (nebula-api)

```
nebula-api/
├── src/main/java/
│   └── com/nebula/api/
│       ├── controller/                       # REST控制器
│       ├── feign/                            # Feign客户端
│       └── config/                           # API配置
└── src/main/resources/
    └── mapper/                                # MyBatis映射
```

## 配置层 (nebula-config)

```
nebula-config/
├── src/main/java/
│   └── com/nebula/config/
│       ├── SecurityConfig.java               # 安全配置
│       ├── MybatisPlusConfig.java            # MyBatis配置
│       ├── RedisConfig.java                  # Redis配置
│       ├── SwaggerConfig.java                # API文档
│       └── SaTokenConfig.java                # 会话管理
└── src/main/resources/
    ├── application.yml                        # 配置
    └── mapper/                                # 映射文件
```

## 公共层 (nebula-common)

```
nebula-common/
├── src/main/java/
│   └── com/nebula/common/
│       ├── annotation/                       # 自定义注解
│       ├── constant/                         # 常量定义
│       ├── exception/                        # 异常处理
│       ├── util/                             # 工具类
│       └── validator/                        # 验证器
└── src/main/resources/
    └── META-INF/
        └── spring.factories                  # Spring配置
```

## 模型层 (nebula-model)

```
nebula-model/
├── src/main/java/
│   └── com/nebula/model/
│       ├── dto/                             # 数据传输对象
│       ├── entity/                          # 数据库实体
│       ├── enums/                           # 枚举类型
│       ├── vo/                              # 值对象
│       └── exception/                       # 自定义异常
└── src/main/resources/
    └── mapper/                               # MyBatis映射
```

## 服务层 (nebula-service)

```
nebula-service/
├── src/main/java/
│   └── com/nebula/service/
│       ├── mapper/                          # MyBatis映射接口
│       ├── service/                         # 服务接口
│       └── impl/                            # 服务实现
└── src/main/resources/
    └── mapper/                               # MyBatis映射
```

## 配置文件结构

```
nebula-backend/
├── nebula-admin/src/main/resources/
│   ├── application.yml                      # 主配置
│   ├── application-dev.yml                  # 开发配置
│   ├── application-prod.yml                 # 生产配置
│   └── .env                                 # 环境变量
├── nebula-config/src/main/resources/
│   ├── application.yml                      # 配置模块
│   └── mapper/                              # 映射文件
└── nebula-service/src/main/resources/
    └── mapper/                              # 映射文件
```

## 测试目录结构

```
src/test/java/
├── com/nebula/api/controller/              # 控制器测试
├── com/nebula/service/                     # 服务测试
│   ├── service/                            # 服务层测试
│   └── mapper/                             # 映射测试
└── com/nebula/common/                      # 公共工具测试
    └── util/                               # 工具测试
```

## 包命名规范

**包结构:**

```
com.nebula.{module}.{submodule}
```

**示例:**

- `com.nebula.api.controller` - API控制器
- `com.nebula.model.entity` - 数据库实体
- `com.nebula.service.impl` - 服务实现
- `com.nebula.common.util` - 公共工具

## 文件命名规范

**命名规则:**

- **类文件:** PascalCase (e.g., `UserService.java`)
- **接口文件:** PascalCase + `Service` (e.g., `UserService.java`)
- **实现文件:** PascalCase + `ServiceImpl` (e.g., `UserServiceImpl.java`)
- **Mapper文件:** PascalCase + `Mapper` (e.g., `UserMapper.java`)
- **DTO文件:** PascalCase + `DTO` (e.g., `UserDTO.java`)
- **VO文件:** PascalCase + `VO` (e.g., `UserVO.java`)

---

*目录结构分析: 2025-03-20*