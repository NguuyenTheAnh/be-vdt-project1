# One-Day Microservices Implementation Guide

## Quick Start Service Template

Each microservice will follow a simplified Spring Boot structure for rapid development:

```
service-name/
├── src/main/java/com/loanmanagement/{service}/
│   ├── ServiceApplication.java
│   ├── config/
│   │   ├── SecurityConfig.java
│   │   └── DatabaseConfig.java
│   ├── controller/
│   ├── service/
│   ├── repository/
│   ├── entity/
│   └── dto/
├── src/main/resources/
│   ├── application.yml
│   └── data.sql (for initial data)
└── Dockerfile (optional for day 1)
```

## 1. Authentication Service Implementation

### Minimal Dependencies (pom.xml)
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
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-api</artifactId>
        <version>0.11.5</version>
    </dependency>
        <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.kafka</groupId>
        <artifactId>spring-kafka</artifactId>
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
    username: ${DB_USERNAME:iam_user}
    password: ${DB_PASSWORD:iam_password}
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
  kafka:
    bootstrap-servers: localhost:9092
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/

jwt:
  secret: ${JWT_SECRET:your-secret-key}
  expiration: 86400000 # 24 hours

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    health:
      show-details: always
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
    
    @EventListener
    public void handleUserCreated(UserCreatedEvent event) {
        try {
            kafkaTemplate.send("user-events", "user.created", event);
            log.info("Published user created event for user: {}", event.getUserId());
        } catch (Exception e) {
            log.error("Failed to publish user created event", e);
        }
    }
    
    @EventListener
    public void handleUserUpdated(UserUpdatedEvent event) {
        kafkaTemplate.send("user-events", "user.updated", event);
    }
}
```

## 2. API Gateway Configuration

### Dependencies
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-gateway</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis-reactive</artifactId>
</dependency>
```

### Gateway Configuration
```yaml
spring:
  application:
    name: api-gateway
  cloud:
    gateway:
      routes:
        - id: iam-service
          uri: lb://iam-service
          predicates:
            - Path=/api/auth/**, /api/users/**, /api/roles/**, /api/permissions/**
          filters:
            - name: RequestRateLimiter
              args:
                redis-rate-limiter.replenish-rate: 10
                redis-rate-limiter.burst-capacity: 20
                
        - id: loan-product-service
          uri: lb://loan-product-service
          predicates:
            - Path=/api/loan-products/**
          filters:
            - AuthenticationFilter
            
        - id: loan-application-service
          uri: lb://loan-application-service
          predicates:
            - Path=/api/loan-applications/**
          filters:
            - AuthenticationFilter
            
        - id: document-service
          uri: lb://document-service
          predicates:
            - Path=/api/documents/**, /api/files/**
          filters:
            - AuthenticationFilter
            
        - id: disbursement-service
          uri: lb://disbursement-service
          predicates:
            - Path=/api/disbursements/**
          filters:
            - AuthenticationFilter
            
        - id: notification-service
          uri: lb://notification-service
          predicates:
            - Path=/api/notifications/**
          filters:
            - AuthenticationFilter
            
        - id: reporting-service
          uri: lb://reporting-service
          predicates:
            - Path=/api/reports/**, /api/analytics/**
          filters:
            - AuthenticationFilter

      default-filters:
        - DedupeResponseHeader=Access-Control-Allow-Credentials Access-Control-Allow-Origin
        - AddResponseHeader=Access-Control-Allow-Origin, *
        - AddResponseHeader=Access-Control-Allow-Methods, GET,POST,PUT,DELETE,OPTIONS
        - AddResponseHeader=Access-Control-Allow-Headers, *
```

### Authentication Filter
```java
@Component
@Slf4j
public class AuthenticationFilter implements GatewayFilter {
    
    private final JwtUtil jwtUtil;
    private final ReactiveRedisTemplate<String, String> redisTemplate;
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        
        if (!containsAuthorizationHeader(request)) {
            return onError(exchange, "Authorization header is missing", HttpStatus.UNAUTHORIZED);
        }
        
        String token = extractToken(request);
        
        return validateToken(token)
            .flatMap(isValid -> {
                if (isValid) {
                    return addUserInfoToRequest(exchange, token)
                        .then(chain.filter(exchange));
                } else {
                    return onError(exchange, "Invalid token", HttpStatus.UNAUTHORIZED);
                }
            });
    }
    
    private Mono<Boolean> validateToken(String token) {
        return redisTemplate.hasKey("blacklist:" + token)
            .map(isBlacklisted -> !isBlacklisted && jwtUtil.validateToken(token));
    }
}
```

## 3. Service Communication Patterns

### Circuit Breaker Implementation
```java
@Component
public class LoanProductClient {
    
    private final WebClient webClient;
    private final CircuitBreaker circuitBreaker;
    
    public LoanProductClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
            .baseUrl("http://loan-product-service")
            .build();
            
        this.circuitBreaker = CircuitBreaker.ofDefaults("loan-product-service");
    }
    
    public Mono<LoanProductDto> getLoanProduct(Long productId) {
        return circuitBreaker.executeSupplier(() ->
            webClient.get()
                .uri("/api/loan-products/{id}", productId)
                .retrieve()
                .bodyToMono(LoanProductDto.class)
                .timeout(Duration.ofSeconds(5))
        ).onErrorReturn(getDefaultLoanProduct());
    }
}
```

### Event-Driven Communication
```java
@KafkaListener(topics = "loan-application-events")
public class LoanApplicationEventHandler {
    
    private final NotificationService notificationService;
    private final DocumentService documentService;
    
    @KafkaHandler
    public void handleApplicationSubmitted(LoanApplicationSubmittedEvent event) {
        log.info("Processing loan application submitted event: {}", event.getApplicationId());
        
        // Send confirmation notification
        notificationService.sendApplicationConfirmation(event);
        
        // Request required documents
        documentService.requestRequiredDocuments(event.getApplicationId(), event.getProductId());
    }
    
    @KafkaHandler
    public void handleApplicationApproved(LoanApplicationApprovedEvent event) {
        log.info("Processing loan application approved event: {}", event.getApplicationId());
        
        // Send approval notification
        notificationService.sendApprovalNotification(event);
        
        // Initiate disbursement process
        disbursementService.initiateDisbursement(event.getApplicationId());
    }
}
```

## 4. Database Migration Scripts

### User Migration Script
```sql
-- Create IAM database and migrate user-related tables
CREATE DATABASE iam_db;

-- Users table
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    phone_number VARCHAR(20),
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Roles table
CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) UNIQUE NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Permissions table
CREATE TABLE permissions (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) UNIQUE NOT NULL,
    description TEXT,
    resource VARCHAR(255),
    action VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- User roles junction table
CREATE TABLE user_roles (
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    role_id BIGINT REFERENCES roles(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

-- Role permissions junction table
CREATE TABLE role_permissions (
    role_id BIGINT REFERENCES roles(id) ON DELETE CASCADE,
    permission_id BIGINT REFERENCES permissions(id) ON DELETE CASCADE,
    PRIMARY KEY (role_id, permission_id)
);

-- Data migration from monolith
INSERT INTO iam_db.users SELECT * FROM loan_db.users;
INSERT INTO iam_db.roles SELECT * FROM loan_db.roles;
INSERT INTO iam_db.permissions SELECT * FROM loan_db.permissions;
INSERT INTO iam_db.user_roles SELECT * FROM loan_db.user_roles;
INSERT INTO iam_db.role_permissions SELECT * FROM loan_db.role_permissions;
```

## 5. Monitoring and Observability

### Application Metrics Configuration
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    health:
      show-details: always
    metrics:
      enabled: true
  metrics:
    export:
      prometheus:
        enabled: true
    tags:
      application: ${spring.application.name}
      environment: ${ENVIRONMENT:local}
```

### Custom Metrics
```java
@Component
public class LoanApplicationMetrics {
    
    private final MeterRegistry meterRegistry;
    private final Counter applicationSubmittedCounter;
    private final Timer applicationProcessingTimer;
    
    public LoanApplicationMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.applicationSubmittedCounter = Counter.builder("loan.applications.submitted")
            .description("Number of loan applications submitted")
            .register(meterRegistry);
        this.applicationProcessingTimer = Timer.builder("loan.applications.processing.time")
            .description("Time taken to process loan applications")
            .register(meterRegistry);
    }
    
    public void incrementApplicationSubmitted() {
        applicationSubmittedCounter.increment();
    }
    
    public Timer.Sample startProcessingTimer() {
        return Timer.start(meterRegistry);
    }
}
```

## 6. Docker Configuration

### Service Dockerfile
```dockerfile
FROM openjdk:21-jre-slim

LABEL maintainer="loan-management-team"

WORKDIR /app

COPY target/*.jar app.jar

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

ENV JAVA_OPTS="-Xmx512m -Xms256m"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
```

### Docker Compose for Local Development
```yaml
version: '3.8'

services:
  zookeeper:
    image: confluentinc/cp-zookeeper:latest
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
      ZOOKEEPER_TICK_TIME: 2000

  kafka:
    image: confluentinc/cp-kafka:latest
    depends_on:
      - zookeeper
    ports:
      - "9092:9092"
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1

  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"

  eureka-server:
    image: loan-management/eureka-server:latest
    ports:
      - "8761:8761"

  config-server:
    image: loan-management/config-server:latest
    ports:
      - "8888:8888"
    depends_on:
      - eureka-server

  api-gateway:
    image: loan-management/api-gateway:latest
    ports:
      - "8080:8080"
    depends_on:
      - eureka-server
      - config-server
      - redis

  iam-service:
    image: loan-management/iam-service:latest
    ports:
      - "8081:8081"
    depends_on:
      - eureka-server
      - config-server
      - kafka
      - iam-db
    environment:
      DB_HOST: iam-db
      DB_USERNAME: iam_user
      DB_PASSWORD: iam_password

  iam-db:
    image: postgres:15
    environment:
      POSTGRES_DB: iam_db
      POSTGRES_USER: iam_user
      POSTGRES_PASSWORD: iam_password
    volumes:
      - iam_db_data:/var/lib/postgresql/data

volumes:
  auth_db_data:
  loan_db_data:
  support_db_data:
```

## 4. Quick Testing Strategy

### Basic Integration Tests
```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class BasicServiceIntegrationTest {
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    void shouldAuthenticateUser() {
        LoginRequest request = new LoginRequest("admin", "password");
        
        ResponseEntity<TokenResponse> response = restTemplate.postForEntity(
            "/auth/login", 
            request, 
            TokenResponse.class
        );
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getToken()).isNotNull();
    }
    
    @Test
    void shouldCreateLoanApplication() {
        // Basic test for loan service
        LoanApplicationRequest request = LoanApplicationRequest.builder()
            .applicantName("John Doe")
            .requestedAmount(BigDecimal.valueOf(10000))
            .build();
        
        ResponseEntity<LoanApplicationResponse> response = restTemplate.postForEntity(
            "/loans/applications", 
            request, 
            LoanApplicationResponse.class
        );
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }
}
```

## 5. One-Day Implementation Checklist

### Hour 1-2: Setup
- [ ] Create 3 Spring Boot projects
- [ ] Configure basic application.yml for each service
- [ ] Setup local databases (H2 for speed, PostgreSQL for production)
- [ ] Basic project structure

### Hour 3-4: Authentication Service
- [ ] Move authentication controllers and services
- [ ] Configure JWT token generation and validation
- [ ] Basic user management endpoints
- [ ] Test login functionality

### Hour 5-6: Loan Service
- [ ] Move loan-related controllers and services
- [ ] Setup loan database schema
- [ ] Implement basic CRUD operations
- [ ] Test loan creation and retrieval

### Hour 7-8: Support Service & Integration
- [ ] Move document and notification services
- [ ] Basic file upload/download
- [ ] Inter-service communication setup
- [ ] End-to-end testing
- [ ] Documentation and rollback preparation

## Quick Deployment Notes

This implementation guide provides a rapid migration path for converting your monolithic loan management system to 3 core microservices within 8 hours. Focus on moving functionality quickly rather than perfect architecture - optimization can be done later.

### Key Shortcuts for One-Day Success:
1. **Use H2 database** for initial setup (faster than PostgreSQL setup)
2. **Skip complex patterns** like Saga or Event Sourcing initially
3. **Direct REST calls** instead of message queues
4. **Shared JWT secret** across all services for simplicity
5. **Minimal error handling** - focus on happy path first
