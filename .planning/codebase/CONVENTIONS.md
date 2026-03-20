# Coding Conventions

**Analysis Date:** 2025-03-20

## Naming Patterns

**Files:**
- Entity classes: PascalCase with descriptive names (e.g., `User.java`, `BlogArticle.java`)
- DTO classes: PascalCase ending with `DTO` (e.g., `UserDTO.java`, `BlogCreateDTO.java`)
- VO classes: PascalCase ending with `VO` (e.g., `UserVO.java`, `BlogArticleVO.java`)
- Service interfaces: PascalCase ending with `Service` (e.g., `UserService.java`)
- Service implementations: PascalCase ending with `ServiceImpl` (e.g., `UserServiceImpl.java`)
- Mapper interfaces: PascalCase ending with `Mapper` (e.g., `UserMapper.java`)
- Controller classes: PascalCase ending with `Controller` (e.g., `UserController.java`)
- Configuration classes: PascalCase ending with `Config` or `Configuration` (e.g., `SecurityConfig.java`)
- Exception classes: PascalCase ending with `Exception` (e.g., `BusinessException.java`)
- Utility classes: Descriptive PascalCase ending with `Util` (e.g., `LogUtil.java`)

**Packages:**
- All lowercase using reverse domain notation: `com.nebula.{module}.{submodule}`
- Examples:
  - `com.nebula.api.controller`
  - `com.nebula.model.entity`
  - `com.nebula.service.impl`
  - `com.nebula.common.util`

**Variables:**
- Instance variables: camelCase (e.g., `username`, `createTime`, `userId`)
- Constants: UPPER_SNAKE_CASE defined in interfaces or enums (e.g., `SUPER_ADMIN_ROLE_ID`)
- Enum values: UPPER_SNAKE_CASE (e.g., `UserStatusEnum.ACTIVE`)

**Methods:**
- camelCase describing action (e.g., `getUserById()`, `createUser()`, `updateUser()`)
- Service methods: verbs describing business operations
- Controller methods: typically map to HTTP verbs (GET/POST/PUT/DELETE)

## Code Style

**Formatting:**
- Implicitly enforced by IDE (no .prettierrc or .eslintrc files)
- Standard Java conventions: 4 spaces for indentation
- Opening braces on same line as declaration
- Blank lines separate logical blocks and methods

**Linting:**
- No explicit linter configuration found
- Relies on IDE inspections and Maven compiler
- No Checkstyle, PMD, or SpotBugs configuration detected

## Import Organization

- Not strictly enforced; imports appear in following general order:
  1. Standard library imports (java.*, javax.*)
  2. Third-party imports (org.*, com.*, lombok.*)
  3. Project imports (com.nebula.*)
- No import order validation tool configured
- No static import blocking

## Module Structure

The project uses a multi-module Maven structure with clear separation of concerns:

```
nebula-backend/
├── nebula-admin/          # Application entry point
├── nebula-api/            # Controllers and Feign clients
├── nebula-config/         # Configuration and cross-cutting concerns
├── nebula-common/         # Shared utilities and annotations
├── nebula-model/          # Data models (entities, DTOs, VOs, enums)
└── nebula-service/        # Business logic and data access
```

## Error Handling

**Strategy:** Centralized exception handling with custom error codes and consistent response format.

**Key Components:**

1. `BusinessException` extends `RuntimeException`:
   - Contains error code and message
   - Constructors accept `ErrorCode` enum or raw code/message
   - Includes convenience factory methods: `unauthorized()`, `userNotFound()`, `systemError()`, etc.
   - `getHttpStatusCode()` maps error codes to HTTP status

2. `GlobalExceptionHandler` with `@RestControllerAdvice`:
   - `@ExceptionHandler(BusinessException.class)` - business errors (uses error code as HTTP status)
   - `@ExceptionHandler(MethodArgumentNotValidException.class)` - validation failures
   - `@ExceptionHandler(BindException.class)` - parameter binding errors
   - `@ExceptionHandler(IllegalArgumentException.class)` - illegal arguments
   - `@ExceptionHandler(RuntimeException.class)` - runtime exceptions with business keyword detection
   - `@ExceptionHandler(Exception.class)` - fallback for all other exceptions

3. Validation:
   - Jakarta Bean Validation annotations on DTO fields: `@NotBlank`, `@Email`, `@Pattern`
   - `@Validated` on controller method parameters
   - Custom validation messages in annotation `message` attribute

4. Error Code Organization (`ErrorCode.java` enum):
   - 1xxx: General errors (PARAM_ERROR, SYSTEM_ERROR)
   - 2xxx: Authentication errors (UNAUTHORIZED, TOKEN_EXPIRED, USER_NOT_FOUND)
   - 3xxx: Registration errors (EMAIL_EXISTS, USERNAME_EXISTS)
   - 4xxx: User-related errors
   - 5xxx: File-related errors
   - 6xxx: OAuth errors
   - 7xxx: Redis errors
   - 8xxx: Database errors
   - 9xxx: Business logic errors (PERMISSION_DENIED, RESOURCE_NOT_FOUND)
   - 10xxx: Permission/System management errors

## Logging

**Framework:** SLF4J with Lombok's `@Slf4j` annotation.

**Pattern:**

1. Class-level logger: `@Slf4j` automatically injects `private static final Logger log`

2. Log levels:
   - `debug`: Detailed flow, Redis operations, field filling
   - `info`: Normal operations: user actions, API requests, database inserts/updates/deletes
   - `warn`: Recoverable issues: login failures, cache misses, business warnings
   - `error`: Exceptions, system failures, API failures

3. Structured logging with placeholders (no string concatenation):
   ```java
   log.info("用户登录成功 | userId={}, email={}", userId, email);
   log.error("业务异常: code={}, message={}", e.getCode(), e.getMessage(), e);
   ```

4. Log utility (`LogUtil.java`):
   - Organized into namespaces: `LogUtil.Auth`, `LogUtil.Redis`, `LogUtil.Api`, `LogUtil.Database`, `LogUtil.Utils`
   - Provides standardized logging methods for common operations
   - Includes data masking: `maskToken()`, `maskEmail()` to protect sensitive data
   - Duration formatting: `formatDuration()` converts milliseconds to readable format

## Function Design

**Service Layer:**
- Interface and implementation separation (e.g., `UserService` interface, `UserServiceImpl` class)
- Implementation extends `ServiceImpl<Mapper, Entity>` from MyBatis Plus for built-in CRUD
- Constructor injection via `@RequiredArgsConstructor` (no `@Autowired` on fields)
- Business logic methods are `public` and transactional as needed
- Service methods return domain types or primitive wrappers, not entities directly when possible

**Controller Layer:**
- `@RestController` for REST APIs
- `@RequestMapping` at class level for base path
- `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping` for HTTP verbs
- Parameters validated with `@Validated` and appropriate binding annotations (`@PathVariable`, `@RequestBody`, `@RequestParam`)
- All methods return `Result<T>` wrapper for consistent response format

**Entity Design:**
- Extend `BaseEntity` for common fields: `createTime`, `updateTime`, `deleted`
- MyBatis Plus annotations: `@TableName`, `@TableId`, `@TableField`
- `@TableLogic` for soft deletes
- `@Data` from Lombok for getters/setters/equals/hashCode/toString
- `@EqualsAndHashCode(callSuper = true)` when extending BaseEntity to include parent fields

**DTO/VO Separation:**
- **DTO (Data Transfer Object):** Request payloads with validation constraints
- **VO (Value Object):** Response payloads for API responses
- **Entity:** Database representation
- Conversion between types using `BeanUtil.copyProperties()` from Hutool

## Cross-Cutting Concerns

**Annotations:**

1. `@RequirePermission(String module, String resource)` - Method-level permission check
2. `@DataScope` - Data scope filtering for multi-tenant queries
3. `@OperationLog` - Audit logging with attributes:
   - `module`: Module name
   - `operation`: Operation description
   - `saveRequestParam`: Whether to log request parameters
   - `saveResponseResult`: Whether to log response data (typically false for sensitive data)

**Automatic Field Filling:**

`MybatisPlusMetaObjectHandler` automatically populates:
- Insert: `createTime`, `updateTime` with current timestamp
- Update: `updateTime` with current timestamp

**Global Constants:**

Defined in interface `AdminConstants`:
- Role IDs and codes (`SUPER_ADMIN_ROLE_ID`, `DEFAULT_USER_ROLE_ID`)
- Menu root ID (`ROOT_MENU_ID`)
- Data scope constants (`ALL_DATA_SCOPE`, `DEFAULT_DATA_SCOPE`)

## Configuration Files

- `application.yml`: Main Spring Boot configuration ( database, Redis, security, Sa-Token)
- `.env`: Environment variables loaded via `spring-dotenv`
- Swagger/OpenAPI 3 enabled at `/v3/api-docs` and `/swagger-ui.html`

## Dependency Injection

- Constructor-based injection exclusively via `@RequiredArgsConstructor`
- No field injection with `@Autowired`
- immutable `final` fields for dependencies
- Service implementations automatically discovered by `@Service`

## Database Conventions

**Naming:**
- Tables: snake_case (e.g., `sys_user`, `blog_article`, `im_message_archive`)
- Column names: snake_case (e.g., `user_id`, `create_time`, `update_time`)
- Foreign keys: `{entity_name}_id` convention

**MyBatis Plus Usage:**
- Base Mapper extends `BaseMapper<Entity>`
- BaseService extends `Service<Mapper, Entity>`
- Use `LambdaQueryWrapper` for type-safe queries
- CRUD methods: `save()`, `updateById()`, `getById()`, `removeById()`, `list()`
- Custom queries in XML mapper files (`src/main/resources/mapper/`)

**Timestamps:**
- `OffsetDateTime` for timezone-aware timestamps
- Automatic filling via `MybatisPlusMetaObjectHandler` and MyBatis Plus `@TableField(fill = ...)`

---

*Convention analysis: 2025-03-20*
