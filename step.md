# Kế hoạch tách Monolithic thành Microservices cho Loan Management System

## 1. PHÂN TÍCH HỆ THỐNG HIỆN TẠI

### 1.1 Cấu trúc Monolithic hiện tại
Dự án hiện tại là một ứng dụng Spring Boot monolithic bao gồm:
- **Controllers**: AuthenticationController, UserController, LoanProductController, LoanApplicationController, DisbursementController, NotificationController, DocumentController, EmailController, FileController, PermissionController, RoleController, SystemConfigurationController, ReportsController, VerificationTokenController
- **Services**: AuthenticationService, UserService, LoanProductService, LoanApplicationService, DisbursementTransactionService, NotificationService, DocumentService, EmailService, FileService, PermissionService, RoleService, SystemConfigurationService, VerificationTokenService
- **Database**: PostgreSQL với 11 bảng chính

### 1.2 Phân tích Domain và Bounded Context
Dựa trên database schema và codebase, có thể xác định các domain chính:
1. **User Management & Authentication** - Quản lý người dùng và xác thực
2. **Loan Product Management** - Quản lý sản phẩm vay
3. **Loan Application Processing** - Xử lý hồ sơ vay
4. **Disbursement Management** - Quản lý giải ngân
5. **Notification System** - Hệ thống thông báo
6. **Document Management** - Quản lý tài liệu
7. **System Configuration** - Cấu hình hệ thống

## 2. THIẾT KẾ KIẾN TRÚC MICROSERVICES

### 2.1 Các Microservices được đề xuất

#### 2.1.1 User Service (Port: 8081)
**Chức năng:**
- Quản lý thông tin người dùng
- Xác thực và phân quyền (JWT)
- Quản lý roles và permissions
- Password reset, account activation

**Database tables:**
- users
- roles
- permissions
- roles_permissions
- verification_tokens
- invalidated_tokens

**APIs chính:**
- POST /auth/login, /auth/logout, /auth/refresh
- GET/POST/PUT/DELETE /users
- GET/POST/PUT/DELETE /roles
- GET/POST/DELETE /permissions
- POST /auth/password-reset, /auth/account-activation

#### 2.1.2 Loan Product Service (Port: 8082)
**Chức năng:**
- Quản lý các sản phẩm vay
- Cấu hình lãi suất, điều kiện vay
- Quản lý trạng thái sản phẩm

**Database tables:**
- loan_products

**APIs chính:**
- GET/POST/PUT/PATCH /loan-products
- GET /loan-products/{id}
- PATCH /loan-products/{id}/status

#### 2.1.3 Loan Application Service (Port: 8083)
**Chức năng:**
- Xử lý hồ sơ vay
- Quản lý trạng thái hồ sơ
- Tích hợp với các service khác để xử lý workflow

**Database tables:**
- loan_applications

**APIs chính:**
- GET/POST/PUT/PATCH/DELETE /loan-applications
- GET /loan-applications/user (hồ sơ của user hiện tại)
- PATCH /loan-applications/{id}/status
- GET /loan-applications/required-documents/{id}

#### 2.1.4 Disbursement Service (Port: 8084)
**Chức năng:**
- Quản lý giải ngân
- Tính toán số tiền đã giải ngân
- Cập nhật trạng thái hồ sơ vay sau giải ngân

**Database tables:**
- disbursement_transactions

**APIs chính:**
- GET/POST/DELETE /disbursements
- GET /disbursements/application/{applicationId}
- GET /disbursements/my
- GET /disbursements/application/{applicationId}/summary

#### 2.1.5 Notification Service (Port: 8085)
**Chức năng:**
- Gửi thông báo cho người dùng
- Quản lý trạng thái đã đọc/chưa đọc
- Email notifications

**Database tables:**
- notifications

**APIs chính:**
- GET/POST/PUT/DELETE /notifications
- PATCH /notifications/mark-all-read
- GET /notifications/unread-count

#### 2.1.6 Document Service (Port: 8086)
**Chức năng:**
- Quản lý tài liệu upload
- File storage và retrieval

**Database tables:**
- documents

**APIs chính:**
- GET/POST/DELETE /documents
- POST /files/upload
- GET /files/{filename}

#### 2.1.7 Configuration Service (Port: 8087)
**Chức năng:**
- Quản lý cấu hình hệ thống
- Reports và statistics

**Database tables:**
- system_configurations

**APIs chính:**
- GET/POST/PUT/DELETE /system-configurations
- GET /reports/statistics

### 2.2 Database Strategy
**Approach: Database per Service**
- Mỗi microservice sẽ có database riêng
- Sử dụng Event-driven communication để đồng bộ dữ liệu
- Shared data thông qua API calls hoặc events

## 3. CHI TIẾT CÁC BƯỚC THỰC HIỆN

### Phase 1: Chuẩn bị Infrastructure (Tuần 1-2)

#### Bước 1.1: Setup Service Discovery
```bash
# Tạo Eureka Server
spring.application.name=eureka-server
server.port=8761
eureka.client.register-with-eureka=false
eureka.client.fetch-registry=false
```

#### Bước 1.2: Setup API Gateway
```bash
# Tạo Spring Cloud Gateway
spring.application.name=api-gateway
server.port=8080
spring.cloud.gateway.routes[0].id=user-service
spring.cloud.gateway.routes[0].uri=lb://user-service
spring.cloud.gateway.routes[0].predicates[0]=Path=/api/users/**,/api/auth/**
```

#### Bước 1.3: Setup Configuration Server
```bash
# Spring Cloud Config Server
spring.application.name=config-server
server.port=8888
spring.cloud.config.server.git.uri=https://github.com/your-repo/config-repo
```

### Phase 2: Tách User Service (Tuần 3-4)

#### Bước 2.1: Tạo User Service Module
```bash
# Tạo project mới
mkdir user-service
cd user-service
# Copy code liên quan: AuthenticationController, UserController, RoleController, PermissionController
# Copy services: AuthenticationService, UserService, RoleService, PermissionService
# Copy entities: User, Role, Permission, VerificationToken, InvalidatedToken
```

#### Bước 2.2: Database Migration cho User Service
```sql
-- Tạo database mới cho User Service
CREATE DATABASE user_service_db;

-- Migrate tables:
-- users, roles, permissions, roles_permissions, verification_tokens, invalidated_tokens
```

#### Bước 2.3: Update Dependencies
```xml
<!-- user-service pom.xml -->
<dependencies>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-config</artifactId>
    </dependency>
    <!-- Other dependencies... -->
</dependencies>
```

#### Bước 2.4: Configuration
```yaml
# user-service application.yml
spring:
  application:
    name: user-service
  datasource:
    url: jdbc:postgresql://localhost:5432/user_service_db
server:
  port: 8081
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka
```

### Phase 3: Tách Loan Product Service (Tuần 5)

#### Bước 3.1: Tạo Loan Product Service
```bash
mkdir loan-product-service
# Copy LoanProductController, LoanProductService
# Copy entity: LoanProduct
```

#### Bước 3.2: Database Migration
```sql
CREATE DATABASE loan_product_service_db;
-- Migrate table: loan_products
```

#### Bước 3.3: Inter-service Communication
```java
// Trong Loan Application Service, call Loan Product Service
@FeignClient(name = "loan-product-service")
public interface LoanProductClient {
    @GetMapping("/loan-products/{id}")
    LoanProductResponse getLoanProductById(@PathVariable Long id);
}
```

### Phase 4: Tách Loan Application Service (Tuần 6-7)

#### Bước 4.1: Tạo Loan Application Service
```bash
mkdir loan-application-service
# Copy LoanApplicationController, LoanApplicationService
# Copy entity: LoanApplication
```

#### Bước 4.2: Database Migration
```sql
CREATE DATABASE loan_application_service_db;
-- Migrate table: loan_applications
```

#### Bước 4.3: Event-driven Communication
```java
// Publish events khi loan application status thay đổi
@Component
public class LoanApplicationEventPublisher {
    @EventListener
    public void handleLoanApplicationStatusChanged(LoanApplicationStatusChangedEvent event) {
        // Send event to message queue
        rabbitTemplate.convertAndSend("loan.status.changed", event);
    }
}
```

### Phase 5: Tách Disbursement Service (Tuần 8)

#### Bước 5.1: Tạo Disbursement Service
```bash
mkdir disbursement-service
# Copy DisbursementController, DisbursementTransactionService
# Copy entity: DisbursementTransaction
```

#### Bước 5.2: Database Migration
```sql
CREATE DATABASE disbursement_service_db;
-- Migrate table: disbursement_transactions
-- Note: Remove FK constraint, use applicationId as reference
```

#### Bước 5.3: Event Handlers
```java
// Listen to loan approval events
@RabbitListener(queues = "loan.approved.queue")
public void handleLoanApproved(LoanApprovedEvent event) {
    // Disbursement logic here
}
```

### Phase 6: Tách Notification Service (Tuần 9)

#### Bước 6.1: Tạo Notification Service
```bash
mkdir notification-service
# Copy NotificationController, NotificationService, EmailService
# Copy entity: Notification
```

#### Bước 6.2: Database Migration
```sql
CREATE DATABASE notification_service_db;
-- Migrate table: notifications
```

#### Bước 6.3: Event-driven Notifications
```java
// Listen to various events and send notifications
@RabbitListener(queues = "loan.status.changed.queue")
public void handleLoanStatusChanged(LoanStatusChangedEvent event) {
    // Send notification to user
}

@RabbitListener(queues = "disbursement.completed.queue")
public void handleDisbursementCompleted(DisbursementCompletedEvent event) {
    // Send email notification
}
```

### Phase 7: Tách Document Service (Tuần 10)

#### Bước 7.1: Tạo Document Service
```bash
mkdir document-service
# Copy DocumentController, DocumentService, FileController, FileService
# Copy entity: Document
```

#### Bước 7.2: Database Migration
```sql
CREATE DATABASE document_service_db;
-- Migrate table: documents
```

### Phase 8: Tách Configuration Service (Tuần 11)

#### Bước 8.1: Tạo Configuration Service
```bash
mkdir configuration-service
# Copy SystemConfigurationController, ReportsController
# Copy SystemConfigurationService
# Copy entity: SystemConfiguration
```

#### Bước 8.2: Database Migration
```sql
CREATE DATABASE configuration_service_db;
-- Migrate table: system_configurations
```

## 4. COMMUNICATION PATTERNS

### 4.1 Synchronous Communication (REST API calls)
```java
// User Service client trong các service khác
@FeignClient(name = "user-service")
public interface UserServiceClient {
    @GetMapping("/users/{id}")
    UserResponse getUserById(@PathVariable Long id);
    
    @GetMapping("/auth/validate")
    boolean validateToken(@RequestHeader("Authorization") String token);
}
```

### 4.2 Asynchronous Communication (Events)
```java
// Event classes
public class LoanApplicationCreatedEvent {
    private Long applicationId;
    private Long userId;
    private Long productId;
    // ... other fields
}

public class LoanApplicationStatusChangedEvent {
    private Long applicationId;
    private String oldStatus;
    private String newStatus;
    private String reason;
}

public class DisbursementCompletedEvent {
    private Long applicationId;
    private Long amount;
    private boolean isFullyDisbursed;
}
```

### 4.3 Message Queue Setup (RabbitMQ)
```yaml
# docker-compose.yml
version: '3.8'
services:
  rabbitmq:
    image: rabbitmq:3-management
    ports:
      - "5672:5672"
      - "15672:15672"
    environment:
      RABBITMQ_DEFAULT_USER: admin
      RABBITMQ_DEFAULT_PASS: admin123
```

## 5. SECURITY STRATEGY

### 5.1 JWT Token Validation
```java
// Shared security configuration
@Component
public class JwtTokenValidator {
    @Autowired
    private UserServiceClient userServiceClient;
    
    public boolean validateToken(String token) {
        return userServiceClient.validateToken("Bearer " + token);
    }
}
```

### 5.2 Service-to-Service Authentication
```java
// Internal service token
@Component
public class ServiceTokenGenerator {
    public String generateServiceToken(String serviceName) {
        // Generate internal service token
    }
}
```

## 6. MONITORING VÀ LOGGING

### 6.1 Distributed Tracing
```xml
<!-- Add to all services -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-sleuth</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-sleuth-zipkin</artifactId>
</dependency>
```

### 6.2 Centralized Logging
```yaml
# logstash configuration
spring:
  application:
    name: ${SERVICE_NAME}
logging:
  pattern:
    console: "%d{HH:mm:ss.SSS} [%thread] %-5level [%X{traceId:-},%X{spanId:-}] %logger{36} - %msg%n"
```

## 7. TESTING STRATEGY

### 7.1 Unit Tests
- Mỗi service cần có unit tests riêng
- Mock external service calls

### 7.2 Integration Tests
```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
    "eureka.client.enabled=false",
    "spring.cloud.config.enabled=false"
})
public class LoanApplicationServiceIntegrationTest {
    // Test with embedded database
}
```

### 7.3 Contract Testing
```java
// Using Spring Cloud Contract
@AutoConfigureStubRunner(ids = "com.vdt:user-service:+:stubs:8081")
public class UserServiceContractTest {
    // Verify contracts between services
}
```

## 8. DEPLOYMENT STRATEGY

### 8.1 Docker Containerization
```dockerfile
# Dockerfile cho mỗi service
FROM openjdk:21-jdk-slim
VOLUME /tmp
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

### 8.2 Docker Compose for Development
```yaml
version: '3.8'
services:
  eureka-server:
    build: ./eureka-server
    ports:
      - "8761:8761"
  
  user-service:
    build: ./user-service
    depends_on:
      - eureka-server
      - user-db
    environment:
      - EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE=http://eureka-server:8761/eureka
  
  user-db:
    image: postgres:15
    environment:
      POSTGRES_DB: user_service_db
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: password
    ports:
      - "5433:5432"
```

### 8.3 Kubernetes Deployment (Production)
```yaml
# user-service-deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: user-service
spec:
  replicas: 3
  selector:
    matchLabels:
      app: user-service
  template:
    metadata:
      labels:
        app: user-service
    spec:
      containers:
      - name: user-service
        image: loan-management/user-service:latest
        ports:
        - containerPort: 8081
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "production"
```

## 9. MIGRATION STRATEGY

### 9.1 Strangler Fig Pattern
1. **Phase 1**: Tạo services mới song song với monolith
2. **Phase 2**: Redirect traffic từng phần đến services mới
3. **Phase 3**: Retire monolith components từng phần

### 9.2 Data Migration
```java
// Data synchronization tool
@Component
public class DataMigrationTool {
    public void migrateUserData() {
        // Copy data from monolith DB to user service DB
        // Verify data integrity
        // Switch traffic to new service
    }
}
```

## 10. ROLLBACK PLAN

### 10.1 Circuit Breaker Pattern
```java
@Component
public class UserServiceCircuitBreaker {
    @CircuitBreaker(name = "user-service", fallbackMethod = "fallbackUser")
    public UserResponse getUser(Long userId) {
        return userServiceClient.getUserById(userId);
    }
    
    public UserResponse fallbackUser(Long userId, Exception ex) {
        // Fallback to monolith or cache
        return monolithUserService.getUserById(userId);
    }
}
```

### 10.2 Feature Toggles
```java
@Component
public class FeatureToggleService {
    @Value("${feature.use-microservices:false}")
    private boolean useMicroservices;
    
    public UserResponse getUser(Long userId) {
        if (useMicroservices) {
            return userServiceClient.getUserById(userId);
        } else {
            return monolithUserService.getUserById(userId);
        }
    }
}
```

## 11. TIMELINE TỔNG QUAN

| Tuần | Nhiệm vụ | Deliverable |
|------|----------|-------------|
| 1-2 | Infrastructure Setup | Eureka, Gateway, Config Server |
| 3-4 | User Service | Authentication & User Management |
| 5 | Loan Product Service | Product Management APIs |
| 6-7 | Loan Application Service | Application Processing |
| 8 | Disbursement Service | Disbursement Management |
| 9 | Notification Service | Notification & Email System |
| 10 | Document Service | File Management |
| 11 | Configuration Service | System Config & Reports |
| 12 | Testing & Monitoring | Complete testing suite |
| 13-14 | Deployment & Migration | Production deployment |

## 12. RISKS VÀ MITIGATION

### 12.1 Technical Risks
- **Data Consistency**: Sử dụng Saga pattern cho distributed transactions
- **Network Latency**: Implement caching strategies
- **Service Failures**: Circuit breaker và retry mechanisms

### 12.2 Business Risks
- **Downtime**: Blue-green deployment strategy
- **Data Loss**: Comprehensive backup và monitoring
- **Performance**: Load testing trước khi migration

## 13. SUCCESS METRICS

- **Performance**: Response time < 200ms cho 95% requests
- **Availability**: 99.9% uptime cho mỗi service
- **Scalability**: Có thể scale từng service độc lập
- **Maintainability**: Deployment time < 10 minutes cho mỗi service
- **Monitoring**: Complete observability với tracing và metrics

---

**Lưu ý**: Đây là kế hoạch chi tiết có thể điều chỉnh dựa trên requirements cụ thể và constraints của project. Recommend bắt đầu với Phase 1 và thực hiện từng bước một cách cẩn thận.
