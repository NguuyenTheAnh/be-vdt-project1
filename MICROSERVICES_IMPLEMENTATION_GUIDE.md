# Microservices Implementation Guide - Dự Án Cá Nhân

## Tổng Quan
Guide này được thiết kế cho developer làm việc một mình, với focus vào:
- Learning microservices patterns
- Practical implementation với Spring Boot/Cloud
- Step-by-step approach phù hợp cho personal project
- Code examples có thể copy và modify

## Service Template Structure

Mỗi microservice sẽ follow cấu trúc Spring Boot chuẩn, simplified cho learning:

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
│   └── db/migration/ (nếu dùng Flyway)
├── src/test/java/
│   └── integration và unit tests
├── Dockerfile (cho containerization)
└── README.md (document service-specific info)
```

## 1. IAM Service Implementation

### Dependencies (pom.xml)
```xml
<dependencies>
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
        <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-config</artifactId>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-api</artifactId>
        <version>0.11.5</version>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-impl</artifactId>
        <version>0.11.5</version>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-jackson</artifactId>
        <version>0.11.5</version>
    </dependency>
</dependencies>
```

### Configuration (application.yml)
```yaml
server:
  port: 8081

spring:
  application:
    name: iam-service
  datasource:
    url: jdbc:postgresql://localhost:5432/iam_db
    username: iam_user
    password: iam_password
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
  cloud:
    config:
      uri: http://localhost:8888
      name: iam-service

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/

jwt:
  secret: ${JWT_SECRET:mySecretKey}
  expiration: 86400000
```

### JWT Token Service
```java
@Service
@Slf4j
public class JwtTokenService {
    
    @Value("${jwt.secret}")
    private String jwtSecret;
    
    @Value("${jwt.expiration}")
    private long jwtExpirationMs;
    
    private final ApplicationEventPublisher eventPublisher;
    
    public String generateToken(UserPrincipal userPrincipal) {
        Date expiryDate = new Date(System.currentTimeMillis() + jwtExpirationMs);
        
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userPrincipal.getId());
        claims.put("roles", userPrincipal.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList()));
        
        String token = Jwts.builder()
            .setClaims(claims)
            .setSubject(userPrincipal.getUsername())
            .setIssuedAt(new Date())
            .setExpiration(expiryDate)
            .signWith(SignatureAlgorithm.HS512, jwtSecret)
            .compact();
            
        // Publish token generated event
        eventPublisher.publishEvent(new TokenGeneratedEvent(userPrincipal.getId(), token));
        
        return token;
    }
    
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }
}
```

### Event Publishing
```java
@Component
@Slf4j
public class UserEventPublisher {
    
    private final KafkaTemplate<String, Object> kafkaTemplate;
    
    public UserEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
    
    @EventListener
    public void handleUserCreatedEvent(UserCreatedEvent event) {
        log.info("Publishing user created event for user: {}", event.getUserId());
        kafkaTemplate.send("user-events", event);
    }
    
    @EventListener
    public void handleUserUpdatedEvent(UserUpdatedEvent event) {
        log.info("Publishing user updated event for user: {}", event.getUserId());
        kafkaTemplate.send("user-events", event);
    }
}
```

## 2. Loan Product Service Implementation

### Application Configuration
```yaml
server:
  port: 8082

spring:
  application:
    name: loan-product-service
  datasource:
    url: jdbc:postgresql://localhost:5432/loan_product_db
    username: loan_product_user
    password: loan_product_password
  jpa:
    hibernate:
      ddl-auto: validate

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/

feign:
  client:
    config:
      iam-service:
        connectTimeout: 5000
        readTimeout: 5000
```

### Product Controller
```java
@RestController
@RequestMapping("/loan-products")
@Validated
@Slf4j
public class LoanProductController {
    
    private final LoanProductService loanProductService;
    private final ProductEligibilityService eligibilityService;
    
    @GetMapping
    public ResponseEntity<List<LoanProductDto>> getAllProducts() {
        List<LoanProductDto> products = loanProductService.getAllProducts();
        return ResponseEntity.ok(products);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<LoanProductDto> getProduct(@PathVariable Long id) {
        LoanProductDto product = loanProductService.getProductById(id);
        return ResponseEntity.ok(product);
    }
    
    @PostMapping("/{id}/eligibility")
    public ResponseEntity<EligibilityResultDto> checkEligibility(
            @PathVariable Long id,
            @RequestBody @Valid EligibilityRequestDto request) {
        
        EligibilityResultDto result = eligibilityService.checkEligibility(id, request);
        return ResponseEntity.ok(result);
    }
}
```

### Feign Client for IAM Service
```java
@FeignClient(name = "iam-service", path = "/users")
public interface IamServiceClient {
    
    @GetMapping("/{userId}")
    UserDto getUserById(@PathVariable Long userId);
    
    @GetMapping("/{userId}/profile")
    UserProfileDto getUserProfile(@PathVariable Long userId);
}
```

## 3. Document Service Implementation

### File Storage Configuration
```java
@Configuration
@EnableConfigurationProperties(FileStorageProperties.class)
public class FileStorageConfig {
    
    private final FileStorageProperties fileStorageProperties;
    
    public FileStorageConfig(FileStorageProperties fileStorageProperties) {
        this.fileStorageProperties = fileStorageProperties;
    }
    
    @Bean
    public FileStorageService fileStorageService() {
        String storageType = fileStorageProperties.getType();
        
        switch (storageType.toLowerCase()) {
            case "s3":
                return new S3FileStorageService(fileStorageProperties.getS3());
            case "local":
                return new LocalFileStorageService(fileStorageProperties.getLocal());
            default:
                throw new IllegalArgumentException("Unsupported storage type: " + storageType);
        }
    }
}
```

### Document Upload Controller
```java
@RestController
@RequestMapping("/documents")
@Slf4j
public class DocumentController {
    
    private final DocumentService documentService;
    private final FileStorageService fileStorageService;
    
    @PostMapping("/upload")
    public ResponseEntity<DocumentDto> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("documentType") String documentType,
            @RequestParam("entityId") Long entityId,
            @RequestParam("entityType") String entityType) {
        
        try {
            // Validate file
            documentService.validateFile(file, documentType);
            
            // Store file
            String fileKey = fileStorageService.store(file);
            
            // Save document metadata
            DocumentDto document = documentService.saveDocument(
                file.getOriginalFilename(),
                fileKey,
                documentType,
                entityId,
                entityType
            );
            
            return ResponseEntity.ok(document);
            
        } catch (Exception e) {
            log.error("Error uploading document", e);
            throw new DocumentUploadException("Failed to upload document", e);
        }
    }
    
    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadDocument(@PathVariable Long id) {
        DocumentDto document = documentService.getDocument(id);
        Resource resource = fileStorageService.load(document.getFileKey());
        
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, 
                "attachment; filename=\"" + document.getOriginalName() + "\"")
            .body(resource);
    }
}
```

## 4. API Gateway Configuration

### Gateway Routes Configuration
```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: iam-service
          uri: lb://iam-service
          predicates:
            - Path=/auth/**, /users/**, /roles/**, /permissions/**
          filters:
            - name: RequestRateLimiter
              args:
                redis-rate-limiter.replenishRate: 10
                redis-rate-limiter.burstCapacity: 20
        
        - id: loan-product-service
          uri: lb://loan-product-service
          predicates:
            - Path=/loan-products/**
          filters:
            - name: AuthenticationFilter
        
        - id: loan-application-service
          uri: lb://loan-application-service
          predicates:
            - Path=/loan-applications/**
          filters:
            - name: AuthenticationFilter
        
        - id: document-service
          uri: lb://document-service
          predicates:
            - Path=/documents/**, /files/**
          filters:
            - name: AuthenticationFilter
```

### Authentication Filter
```java
@Component
@Slf4j
public class AuthenticationFilter implements GatewayFilter {
    
    private final JwtTokenValidator jwtTokenValidator;
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return onError(exchange, "Missing or invalid authorization header", HttpStatus.UNAUTHORIZED);
        }
        
        String token = authHeader.substring(7);
        
        return jwtTokenValidator.validateToken(token)
            .flatMap(isValid -> {
                if (isValid) {
                    return chain.filter(exchange);
                } else {
                    return onError(exchange, "Invalid token", HttpStatus.UNAUTHORIZED);
                }
            });
    }
    
    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        return response.setComplete();
    }
}
```

## 5. Service Discovery (Eureka Server)

### Eureka Server Configuration
```java
@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
    }
}
```

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
  server:
    enable-self-preservation: false
```

## 6. Configuration Server

### Config Server Setup
```java
@SpringBootApplication
@EnableConfigServer
public class ConfigServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication.class, args);
    }
}
```

```yaml
server:
  port: 8888

spring:
  cloud:
    config:
      server:
        git:
          uri: https://github.com/your-org/config-repo
          clone-on-start: true
        native:
          search-locations: classpath:/config
```

## 7. Docker Configuration

### Service Dockerfile Template
```dockerfile
FROM openjdk:21-jre-slim

WORKDIR /app

COPY target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Docker Compose for Development
```yaml
version: '3.8'
services:
  eureka-server:
    build: ./eureka-server
    ports:
      - "8761:8761"
    environment:
      - SPRING_PROFILES_ACTIVE=docker

  config-server:
    build: ./config-server
    ports:
      - "8888:8888"
    depends_on:
      - eureka-server
    environment:
      - EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE=http://eureka-server:8761/eureka

  api-gateway:
    build: ./api-gateway
    ports:
      - "8080:8080"
    depends_on:
      - eureka-server
      - config-server
    environment:
      - EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE=http://eureka-server:8761/eureka
      - SPRING_CLOUD_CONFIG_URI=http://config-server:8888

  iam-service:
    build: ./iam-service
    ports:
      - "8081:8081"
    depends_on:
      - eureka-server
      - config-server
      - iam-db
    environment:
      - EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE=http://eureka-server:8761/eureka
      - SPRING_CLOUD_CONFIG_URI=http://config-server:8888
      - SPRING_DATASOURCE_URL=jdbc:postgresql://iam-db:5432/iam_db
      - SPRING_DATASOURCE_USERNAME=iam_user
      - SPRING_DATASOURCE_PASSWORD=iam_password

  iam-db:
    image: postgres:15
    environment:
      POSTGRES_DB: iam_db
      POSTGRES_USER: iam_user
      POSTGRES_PASSWORD: iam_password
    volumes:
      - iam_db_data:/var/lib/postgresql/data

volumes:
  iam_db_data:
```

## 7. Testing Strategy

### Integration Tests
```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
@Testcontainers
class LoanApplicationServiceIntegrationTest {
    
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("test_db")
            .withUsername("test")
            .withPassword("test");
    
    @Container
    static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest"));
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    void shouldSubmitLoanApplication() {
        // Given
        LoanApplicationRequest request = LoanApplicationRequest.builder()
            .applicantName("John Doe")
            .requestedAmount(BigDecimal.valueOf(10000))
            .productId(1L)
            .build();
        
        // When
        ResponseEntity<LoanApplicationResponse> response = restTemplate.postForEntity(
            "/api/loan-applications", 
            request, 
            LoanApplicationResponse.class
        );
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getStatus()).isEqualTo("SUBMITTED");
    }
}
```

This implementation guide provides the technical foundation for migrating your monolithic loan management system to microservices. Each service follows Spring Boot best practices with proper configuration, monitoring, and testing strategies.
