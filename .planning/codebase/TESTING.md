# Testing Patterns

**Analysis Date:** 2025-03-20

## Test Framework

**Runner:** No test framework detected in current codebase.

**Analysis:**
- Spring Boot Test dependency present in `nebula-admin` module (`spring-boot-starter-test`)
- Test directories exist (`src/test/java`) but are empty
- No testing configuration files found (`junit-platform.properties`, `testng.xml`)
- No test runner annotations or test classes discovered

## Test File Organization

**Location:** Standard Maven structure but empty:
```
nebula-api/src/test/java/
nebula-config/src/test/java/
nebula-service/src/test/java/
nebula-admin/src/test/java/
```

**Naming:** No files found to establish conventions.

## Test Structure

**Framework:** Not implemented.

**Coverage Requirements:** No coverage requirements configured.

## Mocking

**Framework:** Not implemented.

**Patterns:** No mocking patterns established.

## Fixtures and Factories

**Test Data:** No test data fixtures or factories found.

**Location:** No test data directories detected.

## Test Types

**Unit Tests:** Not implemented.

**Integration Tests:** Not implemented.

**E2E Tests:** Not implemented.

## Common Patterns

**Async Testing:** Not implemented.

**Error Testing:** Not implemented.

## Recommendations

Based on the codebase structure and dependencies, here are recommended testing patterns that should be implemented:

### Recommended Test Framework Setup

1. **Dependencies to add to `pom.xml`:**
   ```xml
   <dependency>
       <groupId>org.junit.jupiter</groupId>
       <artifactId>junit-jupiter</artifactId>
       <scope>test</scope>
   </dependency>
   <dependency>
       <groupId>org.mockito</groupId>
       <artifactId>mockito-core</artifactId>
       <scope>test</scope>
   </dependency>
   <dependency>
       <groupId>org.mockito</groupId>
       <artifactId>mockito-junit-jupiter</artifactId>
       <scope>test</scope>
   </dependency>
   <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-test</artifactId>
       <scope>test</scope>
   </dependency>
   ```

2. **Test Structure (when implemented):**
   ```
src/test/java/
├── com/nebula/api/controller/
│   └── UserControllerTest.java
├── com/nebula/service/
│   ├── service/
│   │   └── UserServiceTest.java
│   └── mapper/
│       └── UserMapperTest.java
└── com/nebula/common/
    └── util/
        └── LogUtilTest.java
   ```

3. **Recommended Testing Patterns:**

**Controller Tests:**
```java
@ExtendWith(MockitoExtension.class)
@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    void getUser_ExistingUser_ReturnsUserVO() throws Exception {
        // Given
        UserVO expected = new UserVO(1L, "testuser", "Test User");
        when(userService.getUserVO(1L)).thenReturn(expected);

        // When & Then
        mockMvc.perform(get("/api/users/1"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.code").value(200))
               .andExpect(jsonPath("$.data.id").value(1))
               .andExpect(jsonPath("$.data.username").value("testuser"));
    }

    @Test
    void createUser_ValidInput_ReturnsUserId() throws Exception {
        // Given
        UserDTO userDTO = new UserDTO(null, "newuser", "password123", "New User");
        when(userService.createUser(any(UserDTO.class))).thenReturn(2L);

        // When & Then
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"newuser\",\"password\":\"password123\",\"nickname\":\"New User\"}"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.code").value(200))
               .andExpect(jsonPath("$.message").value("创建成功"))
               .andExpect(jsonPath("$.data").value(2));
    }
}
```

**Service Tests:**
```java
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void createUser_ValidInput_CreatesUser() {
        // Given
        UserDTO userDTO = new UserDTO(null, "testuser", "password123", "Test User");
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userMapper.insert(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return 1;
        });

        // When
        Long id = userService.createUser(userDTO);

        // Then
        assertEquals(1L, id);
        verify(userMapper).insert(argThat(user ->
            "testuser".equals(user.getUsername()) &&
            "encodedPassword".equals(user.getPassword())
        ));
    }

    @Test
    void createUser_NullPassword_DoesNotEncode() {
        // Given
        UserDTO userDTO = new UserDTO(null, "testuser", null, "Test User");

        // When
        Long id = userService.createUser(userDTO);

        // Then
        verify(passwordEncoder, never()).encode(any());
    }
}
```

**Mapper Tests:**
```java
@ExtendWith(SpringExtension.class)
@SpringBootTest
@MapperTest(UserMapper.class)
class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    void insert_User_SavesUser() {
        // Given
        User user = new User(null, "testuser", "password", "Test User", "test@example.com");

        // When
        int result = userMapper.insert(user);

        // Then
        assertEquals(1, result);
        assertNotNull(user.getId());
    }

    @Test
    void selectById_ExistingUser_ReturnsUser() {
        // Given
        User user = new User(null, "testuser", "password", "Test User", "test@example.com");
        userMapper.insert(user);

        // When
        User found = userMapper.selectById(user.getId());

        // Then
        assertNotNull(found);
        assertEquals("testuser", found.getUsername());
    }
}
```

**Utility Tests:**
```java
@ExtendWith(MockitoExtension.class)
class LogUtilTest {

    @Test
    void maskToken_ShowsFirstSixAndLastFour() {
        String token = "abcdefghijklmnopqrstuvwxyz1234567890";
        String masked = LogUtil.Utils.maskToken(token);
        assertEquals("abcdef...7890", masked);
    }

    @Test
    void maskEmail_HidesMiddle() {
        String email = "user@example.com";
        String masked = LogUtil.Utils.maskEmail(email);
        assertEquals("us***@example.com", masked);
    }

    @Test
    void formatDuration_Milliseconds() {
        String result = LogUtil.Utils.formatDuration(500);
        assertEquals("500ms", result);
    }

    @Test
    void formatDuration_Seconds() {
        String result = LogUtil.Utils.formatDuration(1500);
        assertEquals("1.50s", result);
    }
}
```

4. **Coverage Requirements:**
   - Recommended: 80% line coverage for business logic
   - 100% coverage for utility classes
   - 70% coverage for integration layers

5. **Running Tests:**
   ```bash
   mvn test                    # Run all tests
   mvn test -Dtest=UserServiceTest  # Run specific test
   mvn test jacoco:report      # Generate coverage report
   ```

6. **Test Categories (when implemented):**
   ```java
   @Tag("unit")           // Unit tests - fast, isolated
   @Tag("integration")    // Integration tests - DB, external services
   @Tag("slow")          // Tests that take > 1s
   @Tag("database")      // Tests requiring database
   ```

---

*Testing analysis: 2025-03-20*
