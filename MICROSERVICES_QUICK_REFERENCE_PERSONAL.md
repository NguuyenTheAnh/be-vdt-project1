# Microservices Quick Reference - Dự Án Cá Nhân

## Service Overview (Simplified for Learning)

### Infrastructure Services
```
┌─────────────────┐    ┌─────────────────┐
│ Eureka Server   │    │   API Gateway   │
│   (Port 8761)   │    │   (Port 8080)   │
│   [Required]    │    │   [Required]    │
└─────────────────┘    └─────────────────┘
```

### Business Services (Build in Order)
```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   IAM Service   │    │ Loan Product    │    │Document Service │
│   (Port 8081)   │    │   (Port 8082)   │    │   (Port 8083)   │
│   [Phase 1]     │    │   [Phase 2]     │    │   [Phase 3]     │
└─────────────────┘    └─────────────────┘    └─────────────────┘

┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│Loan Application │    │ Notification    │    │ Reporting       │
│   (Port 8084)   │    │   (Port 8086)   │    │   (Port 8087)   │
│   [Phase 4]     │    │   [Phase 5]     │    │   [Phase 6]     │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

## Development Workflow

### Daily Startup Sequence
```bash
# 1. Start Eureka Server (Terminal 1)
cd eureka-server
./mvnw spring-boot:run

# 2. Start API Gateway (Terminal 2) 
cd api-gateway
./mvnw spring-boot:run

# 3. Start your current service (Terminal 3)
cd iam-service  # hoặc service đang develop
./mvnw spring-boot:run

# 4. Verify services registered
# Open: http://localhost:8761
```

### Development Ports
- **Eureka Server**: 8761
- **API Gateway**: 8080
- **IAM Service**: 8081
- **Loan Product Service**: 8082
- **Document Service**: 8083
- **Loan Application Service**: 8084
- **Notification Service**: 8086
- **Reporting Service**: 8087

## Quick Commands

### Maven Commands
```bash
# Create new Spring Boot project
./mvnw spring-boot:run

# Clean and compile
./mvnw clean compile

# Run tests
./mvnw test

# Build jar
./mvnw clean package

# Skip tests (for faster builds during development)
./mvnw clean package -DskipTests
```

### Git Workflow for Personal Project
```bash
# Create feature branch
git checkout -b feature/iam-service

# Regular commits
git add .
git commit -m "feat: implement user authentication"

# Merge to main when feature complete
git checkout main
git merge feature/iam-service
```

### Docker Commands (Optional - for later phases)
```bash
# Build service image
docker build -t iam-service .

# Run with Docker Compose
docker-compose up -d

# View logs
docker-compose logs iam-service

# Stop all services
docker-compose down
```

## Testing Endpoints

### IAM Service Endpoints
```bash
# Health check
curl http://localhost:8081/actuator/health

# Register user
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com", 
    "password": "password123",
    "firstName": "Test",
    "lastName": "User"
  }'

# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }'

# Test protected endpoint (replace YOUR_JWT_TOKEN)
curl -X GET http://localhost:8080/api/test/private \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Default Test Users
- **Admin**: username: `admin`, password: `admin123`
- **Test User**: Create via registration endpoint

## Database Access

### H2 Console (Development)
- **URL**: http://localhost:8081/h2-console
- **JDBC URL**: `jdbc:h2:mem:testdb`
- **Username**: `sa`
- **Password**: `password`

### Useful SQL Queries
```sql
-- View all users
SELECT * FROM users;

-- View user roles
SELECT u.username, r.name as role 
FROM users u 
JOIN user_roles ur ON u.id = ur.user_id 
JOIN roles r ON ur.role_id = r.id;

-- Reset user password (encoded for 'newpassword')
UPDATE users SET password = '$2a$10$...' WHERE username = 'testuser';
```

## Troubleshooting

### Service Won't Start
```bash
# Check if port is in use
netstat -an | findstr :8081

# Kill process on port (Windows)
netstat -ano | findstr :8081
taskkill /PID <PID> /F

# Check logs for errors
./mvnw spring-boot:run | grep ERROR
```

### Service Not Registering with Eureka
1. Verify Eureka server is running (http://localhost:8761)
2. Check `eureka.client.service-url.defaultZone` in application.yml
3. Look for connection errors in logs
4. Ensure service name is unique

### JWT Token Issues
```bash
# Decode JWT token (useful for debugging)
# Use online tool: https://jwt.io

# Common issues:
# - Token expired (check expiration time)
# - Invalid signature (check secret key)
# - Missing Authorization header
```

### H2 Database Issues
```bash
# Can't connect to H2 console
# 1. Check service is running
# 2. Verify URL: http://localhost:8081/h2-console
# 3. Use correct JDBC URL: jdbc:h2:mem:testdb

# Data not persisting
# This is normal with H2 in-memory database
# Data resets on service restart
```

### API Gateway Issues
```bash
# Requests not routing
# 1. Check service registration in Eureka
# 2. Verify gateway routes in application.yml
# 3. Test direct service call first: http://localhost:8081/api/...

# 502 Bad Gateway
# Usually means target service is down
# Check individual service health
```

## Environment Variables

### Development Environment
```bash
# Set JWT secret (optional, has default)
export JWT_SECRET=mySecretKey123456789012345678901234567890

# Set database URL (when switching to PostgreSQL)
export DB_URL=jdbc:postgresql://localhost:5432/iam_db
export DB_USER=iam_user
export DB_PASSWORD=iam_password
```

## IDE Configuration

### IntelliJ IDEA
- Install Spring Boot plugin
- Enable annotation processing (for Lombok)
- Set up run configurations for each service
- Use HTTP Client for testing APIs

### VS Code
- Install Spring Boot Extension Pack
- Install REST Client extension
- Use integrated terminal for running services

## Learning Progress Tracking

### Phase 1 Checklist ✅
- [ ] Eureka Server running
- [ ] API Gateway routing requests
- [ ] IAM Service authenticating users
- [ ] H2 database storing data
- [ ] JWT tokens working
- [ ] All services registered in Eureka

### Phase 2 Checklist (Next)
- [ ] Loan Product Service created
- [ ] Feign client communication
- [ ] Service-to-service authentication
- [ ] Product CRUD operations

## Useful URLs

### Development URLs
- **Eureka Dashboard**: http://localhost:8761
- **API Gateway**: http://localhost:8080
- **IAM Service Direct**: http://localhost:8081
- **H2 Console**: http://localhost:8081/h2-console

### Documentation
- **Spring Boot**: https://spring.io/projects/spring-boot
- **Spring Cloud**: https://spring.io/projects/spring-cloud
- **Spring Security**: https://spring.io/projects/spring-security

## Performance Tips

### Development Optimization
```bash
# Faster startup - skip tests
./mvnw spring-boot:run -Dspring-boot.run.skip-tests=true

# Reduce JVM memory for development
export MAVEN_OPTS="-Xmx512m -Xms256m"

# Enable DevTools for hot reload
# Add spring-boot-devtools dependency
```

### Resource Management
- Stop unused services to save memory
- Use H2 for development, PostgreSQL for production testing
- Monitor CPU/Memory usage in IDE

## Next Steps Guide

### After IAM Service Working:
1. **Create Loan Product Service**
   - Copy IAM service structure
   - Change package names
   - Implement product CRUD
   - Add Feign client to call IAM

2. **Add Inter-Service Communication**
   - Learn Feign Client
   - Implement service discovery
   - Handle service failures

3. **Implement Document Service**
   - File upload/download
   - Integration with loan applications
   - Learn about file storage patterns

## Common Patterns

### Controller Pattern
```java
@RestController
@RequestMapping("/api/products")
@Slf4j
public class ProductController {
    
    private final ProductService productService;
    
    @GetMapping
    public ResponseEntity<List<ProductDto>> getAllProducts() {
        // Implementation
    }
    
    @PostMapping
    public ResponseEntity<ProductDto> createProduct(@Valid @RequestBody CreateProductRequest request) {
        // Implementation
    }
}
```

### Service Pattern
```java
@Service
@Transactional
@Slf4j
public class ProductService {
    
    private final ProductRepository productRepository;
    
    public List<Product> getAllProducts() {
        // Implementation
    }
    
    public Product createProduct(CreateProductRequest request) {
        // Implementation
    }
}
```

### Repository Pattern
```java
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByStatus(ProductStatus status);
    Optional<Product> findByNameIgnoreCase(String name);
}
```

Remember: Focus trên learning và understanding concepts. Không cần làm perfect ngay từ đầu!
