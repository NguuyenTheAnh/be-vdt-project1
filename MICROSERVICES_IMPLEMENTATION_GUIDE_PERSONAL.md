# Microservices Implementation Guide - Dự Án Cá Nhân

## Tổng Quan
Guide này được thiết kế cho developer làm việc một mình, với focus vào:
- Learning microservices patterns một cách thực tế
- Practical implementation với Spring Boot/Cloud
- Step-by-step approach phù hợp cho personal project
- Code examples có thể copy và modify
- Simplified setup cho learning environment

## Prerequisites
- Java 21+ đã cài đặt
- Docker Desktop running
- IDE (IntelliJ IDEA/VS Code) với Spring Boot extension
- Git knowledge
- Basic understanding của Spring Boot

## Service Template Structure

Mỗi microservice sẽ follow cấu trúc Spring Boot đơn giản:

```
service-name/
├── src/main/java/com/vdt_project1/loanmanagement/{service}/
│   ├── ServiceApplication.java
│   ├── config/
│   │   ├── SecurityConfig.java (nếu cần)
│   │   └── DatabaseConfig.java (nếu cần)
│   ├── controller/
│   ├── service/
│   ├── repository/
│   ├── entity/
│   ├── dto/
│   └── exception/
├── src/main/resources/
│   ├── application.yml
│   └── data.sql (cho test data)
├── src/test/java/
│   └── basic unit tests
├── Dockerfile
└── README.md
```

## Phase 1: Setup Infrastructure Services

### 1. Eureka Service Discovery

**Create new Spring Boot project**: `eureka-server`

**Dependencies (pom.xml)**:
```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
    </dependency>
</dependencies>

<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-dependencies</artifactId>
            <version>2023.0.0</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

**Main Application**:
```java
@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
    }
}
```

**application.yml**:
```yaml
server:
  port: 8761

eureka:
  instance:
    hostname: localhost
  client:
    register-with-eureka: false
    fetch-registry: false
    service-url:
      defaultZone: http://${eureka.instance.hostname}:${server.port}/eureka/

spring:
  application:
    name: eureka-server
```

### 2. API Gateway

**Create new Spring Boot project**: `api-gateway`

**Dependencies**:
```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-gateway</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
    </dependency>
</dependencies>
```

**application.yml**:
```yaml
server:
  port: 8080

spring:
  application:
    name: api-gateway
  cloud:
    gateway:
      discovery:
        locator:
          enabled: true
          lower-case-service-id: true
      routes:
        - id: iam-service
          uri: lb://iam-service
          predicates:
            - Path=/api/auth/**, /api/users/**

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
```

## Phase 2: First Business Service - IAM Service

### Project Setup
**Create new Spring Boot project**: `iam-service`

**Dependencies**:
```xml
<dependencies>
    <!-- Core Spring Boot -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
    
    <!-- Microservices -->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
    </dependency>
    
    <!-- Database - H2 cho development -->
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <scope>runtime</scope>
    </dependency>
    
    <!-- JWT -->
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-api</artifactId>
        <version>0.11.5</version>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-impl</artifactId>
        <version>0.11.5</version>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-jackson</artifactId>
        <version>0.11.5</version>
        <scope>runtime</scope>
    </dependency>
    
    <!-- Utilities -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
</dependencies>
```

### Configuration Files

**application.yml**:
```yaml
server:
  port: 8081

spring:
  application:
    name: iam-service
  
  # H2 Database cho development
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
    username: sa
    password: password
  
  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
    hibernate:
      ddl-auto: create-drop
    show-sql: true
  
  h2:
    console:
      enabled: true
      path: /h2-console

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/

# JWT Configuration
jwt:
  secret: mySecretKey123456789012345678901234567890
  expiration: 86400000  # 24 hours

logging:
  level:
    com.vdt_project1: DEBUG
```

### Core Entities

**User Entity**:
```java
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String username;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String password;
    
    @Column(name = "first_name")
    private String firstName;
    
    @Column(name = "last_name")
    private String lastName;
    
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private UserStatus status = UserStatus.ACTIVE;
    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @Builder.Default
    private Set<Role> roles = new HashSet<>();
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
```

**Role Entity**:
```java
@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String name;
    
    private String description;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
```

**Enums**:
```java
public enum UserStatus {
    ACTIVE, INACTIVE, SUSPENDED, PENDING_VERIFICATION
}
```

### DTOs

**LoginRequest**:
```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    @NotBlank(message = "Username is required")
    private String username;
    
    @NotBlank(message = "Password is required")
    private String password;
}
```

**RegisterRequest**:
```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;
    
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
    
    @NotBlank(message = "First name is required")
    private String firstName;
    
    @NotBlank(message = "Last name is required")
    private String lastName;
}
```

**AuthResponse**:
```java
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {
    private String token;
    private String type = "Bearer";
    private Long userId;
    private String username;
    private String email;
    private Set<String> roles;
    
    public AuthResponse(String token, Long userId, String username, String email, Set<String> roles) {
        this.token = token;
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.roles = roles;
    }
}
```

### Security Configuration

**JWT Utility**:
```java
@Component
@Slf4j
public class JwtUtils {
    
    @Value("${jwt.secret}")
    private String jwtSecret;
    
    @Value("${jwt.expiration}")
    private long jwtExpirationMs;
    
    public String generateJwtToken(UserPrincipal userPrincipal) {
        return generateTokenFromUsername(userPrincipal.getUsername(), userPrincipal);
    }
    
    public String generateTokenFromUsername(String username, UserPrincipal userPrincipal) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userPrincipal.getId());
        claims.put("email", userPrincipal.getEmail());
        claims.put("roles", userPrincipal.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList()));
        
        return Jwts.builder()
            .setClaims(claims)
            .setSubject(username)
            .setIssuedAt(new Date())
            .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
            .signWith(SignatureAlgorithm.HS512, jwtSecret)
            .compact();
    }
    
    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
    }
    
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            log.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }
}
```

**UserPrincipal**:
```java
@Data
@AllArgsConstructor
public class UserPrincipal implements UserDetails {
    private Long id;
    private String username;
    private String email;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;
    
    public static UserPrincipal create(User user) {
        List<GrantedAuthority> authorities = user.getRoles().stream()
            .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
            .collect(Collectors.toList());
        
        return new UserPrincipal(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getPassword(),
            authorities
        );
    }
    
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    
    @Override
    public boolean isEnabled() {
        return true;
    }
}
```

### Services

**UserService**:
```java
@Service
@Transactional
@Slf4j
public class UserService {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    
    public UserService(UserRepository userRepository, 
                      RoleRepository roleRepository,
                      PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    public User createUser(RegisterRequest registerRequest) {
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new RuntimeException("Username is already taken!");
        }
        
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("Email is already taken!");
        }
        
        // Create new user
        User user = User.builder()
            .username(registerRequest.getUsername())
            .email(registerRequest.getEmail())
            .password(passwordEncoder.encode(registerRequest.getPassword()))
            .firstName(registerRequest.getFirstName())
            .lastName(registerRequest.getLastName())
            .status(UserStatus.ACTIVE)
            .build();
        
        // Assign default role
        Role userRole = roleRepository.findByName("USER")
            .orElseThrow(() -> new RuntimeException("Default role not found"));
        user.getRoles().add(userRole);
        
        User savedUser = userRepository.save(user);
        log.info("Created new user: {}", savedUser.getUsername());
        
        return savedUser;
    }
    
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
    
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }
}
```

### Controllers

**AuthController**:
```java
@RestController
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {
    
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtils jwtUtils;
    
    public AuthController(AuthenticationManager authenticationManager,
                         UserService userService,
                         JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtUtils = jwtUtils;
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
                )
            );
            
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            String jwt = jwtUtils.generateJwtToken(userPrincipal);
            
            Set<String> roles = userPrincipal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
            
            AuthResponse authResponse = new AuthResponse(
                jwt,
                userPrincipal.getId(),
                userPrincipal.getUsername(),
                userPrincipal.getEmail(),
                roles
            );
            
            log.info("User {} logged in successfully", loginRequest.getUsername());
            return ResponseEntity.ok(authResponse);
            
        } catch (BadCredentialsException e) {
            log.warn("Login failed for user: {}", loginRequest.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", "Invalid username or password"));
        }
    }
    
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            User user = userService.createUser(registerRequest);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of(
                    "message", "User registered successfully",
                    "userId", user.getId()
                ));
                
        } catch (RuntimeException e) {
            log.error("Registration failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(Map.of("message", e.getMessage()));
        }
    }
}
```

### Data Initialization

**DataInitializer**:
```java
@Component
@Slf4j
public class DataInitializer implements CommandLineRunner {
    
    private final RoleRepository roleRepository;
    private final UserService userService;
    
    public DataInitializer(RoleRepository roleRepository, UserService userService) {
        this.roleRepository = roleRepository;
        this.userService = userService;
    }
    
    @Override
    public void run(String... args) throws Exception {
        initializeRoles();
        initializeDefaultAdmin();
    }
    
    private void initializeRoles() {
        if (roleRepository.count() == 0) {
            roleRepository.save(Role.builder().name("ADMIN").description("Administrator").build());
            roleRepository.save(Role.builder().name("USER").description("Regular User").build());
            roleRepository.save(Role.builder().name("MANAGER").description("Loan Manager").build());
            log.info("Default roles created");
        }
    }
    
    private void initializeDefaultAdmin() {
        if (!userService.findByUsername("admin").isPresent()) {
            RegisterRequest adminRequest = new RegisterRequest();
            adminRequest.setUsername("admin");
            adminRequest.setEmail("admin@example.com");
            adminRequest.setPassword("admin123");
            adminRequest.setFirstName("Admin");
            adminRequest.setLastName("User");
            
            User admin = userService.createUser(adminRequest);
            
            // Add admin role
            Role adminRole = roleRepository.findByName("ADMIN").orElseThrow();
            admin.getRoles().add(adminRole);
            
            log.info("Default admin user created");
        }
    }
}
```

### Testing the IAM Service

**Simple Test Controller** (để test mọi thứ hoạt động):
```java
@RestController
@RequestMapping("/api/test")
public class TestController {
    
    @GetMapping("/public")
    public ResponseEntity<Map<String, String>> publicEndpoint() {
        return ResponseEntity.ok(Map.of(
            "message", "Public endpoint working",
            "timestamp", LocalDateTime.now().toString()
        ));
    }
    
    @GetMapping("/private")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Map<String, String>> privateEndpoint(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        
        return ResponseEntity.ok(Map.of(
            "message", "Private endpoint working",
            "user", userPrincipal.getUsername(),
            "timestamp", LocalDateTime.now().toString()
        ));
    }
}
```

## Testing Your First Service

### 1. Start Services
```bash
# Terminal 1: Start Eureka Server
cd eureka-server
./mvnw spring-boot:run

# Terminal 2: Start IAM Service
cd iam-service
./mvnw spring-boot:run

# Terminal 3: Start API Gateway
cd api-gateway
./mvnw spring-boot:run
```

### 2. Test Endpoints

**Register a user**:
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123",
    "firstName": "Test",
    "lastName": "User"
  }'
```

**Login**:
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }'
```

**Test protected endpoint**:
```bash
# Replace JWT_TOKEN với token từ login response
curl -X GET http://localhost:8080/api/test/private \
  -H "Authorization: Bearer JWT_TOKEN"
```

### 3. Verify Service Registration
- Mở http://localhost:8761 để xem Eureka Dashboard
- Kiểm tra `iam-service` đã registered

## Next Steps

Sau khi IAM Service hoạt động ổn định:

1. **Implement Loan Product Service** - follow tương tự pattern
2. **Add inter-service communication** với Feign Client
3. **Implement Document Service** với file upload
4. **Add centralized configuration** với Config Server
5. **Implement monitoring** với Actuator endpoints

## Common Issues và Solutions

### Port Conflicts
- Kiểm tra ports có bị conflicts không (8080, 8081, 8761)
- Use `netstat -an | findstr :8080` để check

### Service Registration Issues
- Verify Eureka server running trước
- Check application.yml configurations
- Look at logs cho connection errors

### Database Issues
- H2 Console: http://localhost:8081/h2-console
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`, Password: `password`

### JWT Issues
- Verify secret key length (minimum 256 bits)
- Check token expiration time
- Validate token format trong requests

## Learning Resources

- **Spring Cloud Gateway**: https://spring.io/projects/spring-cloud-gateway
- **Eureka**: https://netflix.github.io/eureka/
- **JWT với Spring**: https://jwt.io/introduction/
- **Spring Security**: https://spring.io/projects/spring-security

Remember: Đây là learning project, focus vào understanding concepts hơn là perfect production setup!
