# One-Day Microservices Migration Quick Reference

## Simplified Service Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                    Frontend Application                         │
│                    (Existing - No Changes)                      │
└─────────────────────┬───────────────────────────────────────────┘
                      │
                      ▼
                  Static Routing
                   (No Gateway)

┌──────────────────┐  ┌──────────────────┐  ┌──────────────────┐
│ Authentication   │  │    Loan          │  │   Support        │
│   Service        │  │   Service        │  │   Service        │
│  Port: 8081      │  │  Port: 8082      │  │  Port: 8083      │
│                  │  │                  │  │                  │
│ - User Auth      │  │ - Loan Products  │  │ - Documents      │
│ - JWT Tokens     │  │ - Applications   │  │ - Notifications  │
│ - Basic Users    │  │ - Loan Mgmt      │  │ - Simple Reports │
└──────────────────┘  └──────────────────┘  └──────────────────┘
```

## Service Responsibilities (Simplified)

| Service | Responsibility | Key Components | Database |
|---------|----------------|---------------|----------|
| **Authentication Service** | Login, JWT tokens, basic user management | AuthController, UserService, JwtUtil | auth_db |
| **Loan Service** | Loan products, applications, loan management | LoanController, LoanService, LoanProductService | loan_db |
| **Support Service** | File uploads, notifications, basic reports | DocumentController, NotificationService | support_db |

## Port Assignments & Quick Access

| Service | Port | Health Check URL | Main Endpoints |
|---------|------|------------------|----------------|
| Authentication Service | 8081 | http://localhost:8081/actuator/health | `/auth/login`, `/auth/register`, `/users/*` |
| IAM Service | 8081 | http://localhost:8081/actuator/health |
| Loan Product Service | 8082 | http://localhost:8082/actuator/health |
| Loan Application Service | 8083 | http://localhost:8083/actuator/health |
| Document Service | 8084 | http://localhost:8084/actuator/health |
| Disbursement Service | 8085 | http://localhost:8085/actuator/health |
| Notification Service | 8086 | http://localhost:8086/actuator/health |
| Reporting Service | 8087 | http://localhost:8087/actuator/health |
| Config Server | 8888 | http://localhost:8888/actuator/health |
| Eureka Server | 8761 | http://localhost:8761/ |

## API Endpoints Reference

### Authentication & User Management (IAM Service)
```
POST   /api/auth/login              - User login
POST   /api/auth/refresh            - Refresh token
POST   /api/auth/logout             - User logout
GET    /api/users                   - List users
POST   /api/users                   - Create user
GET    /api/users/{id}              - Get user by ID
PUT    /api/users/{id}              - Update user
DELETE /api/users/{id}              - Delete user
GET    /api/roles                   - List roles
POST   /api/roles                   - Create role
GET    /api/permissions             - List permissions
```

### Loan Products (Loan Product Service)
```
GET    /api/loan-products           - List loan products
POST   /api/loan-products           - Create loan product
GET    /api/loan-products/{id}      - Get loan product
PUT    /api/loan-products/{id}      - Update loan product
DELETE /api/loan-products/{id}      - Delete loan product
GET    /api/loan-products/{id}/eligibility - Check eligibility
```

### Loan Applications (Loan Application Service)
```
GET    /api/loan-applications       - List applications
POST   /api/loan-applications       - Submit application
GET    /api/loan-applications/{id}  - Get application
PUT    /api/loan-applications/{id}  - Update application
POST   /api/loan-applications/{id}/approve - Approve application
POST   /api/loan-applications/{id}/reject  - Reject application
GET    /api/loan-applications/{id}/status  - Get status
```

### Documents (Document Service)
```
GET    /api/documents               - List documents
POST   /api/documents               - Upload document
GET    /api/documents/{id}          - Get document
DELETE /api/documents/{id}          - Delete document
POST   /api/files/upload            - File upload
GET    /api/files/{id}/download     - File download
```

### Disbursements (Disbursement Service)
```
GET    /api/disbursements           - List disbursements
POST   /api/disbursements           - Create disbursement
| Loan Service | 8082 | http://localhost:8082/actuator/health | `/loans/*`, `/loan-products/*`, `/applications/*` |
| Support Service | 8083 | http://localhost:8083/actuator/health | `/documents/*`, `/notifications/*`, `/reports/*` |

## Essential API Endpoints

### Authentication Service (Port 8081)
```
POST   /auth/login                  - User login (returns JWT)
POST   /auth/register               - User registration
POST   /auth/refresh                - Refresh JWT token
GET    /users/profile               - Get user profile
PUT    /users/profile               - Update user profile
```

### Loan Service (Port 8082)
```
GET    /loan-products               - List loan products
GET    /loan-products/{id}          - Get loan product details
POST   /loans/applications          - Create loan application
GET    /loans/applications          - List user's applications
GET    /loans/applications/{id}     - Get application details
PUT    /loans/applications/{id}     - Update application
POST   /loans/applications/{id}/submit - Submit for approval
```

### Support Service (Port 8083)
```
POST   /documents/upload            - Upload document
GET    /documents/{id}              - Download document
DELETE /documents/{id}              - Delete document
POST   /notifications/send          - Send notification
GET    /reports/basic               - Get basic reports
GET    /reports/loans               - Get loan reports
```

## Quick Configuration Reference

### Application Properties (Each Service)
```yaml
server:
  port: 808X  # X = 1,2,3 for respective services

spring:
  application:
    name: service-name
  datasource:
    url: jdbc:h2:mem:testdb  # For quick setup
    # url: jdbc:postgresql://localhost:5432/service_db  # For production
  jpa:
    hibernate:
      ddl-auto: create-drop  # For development
    show-sql: true

jwt:
  secret: mySecretKey  # Same across all services for simplicity
  expiration: 86400000  # 24 hours
```

### Environment Variables (Simplified)
```bash
# Database (if using PostgreSQL)
DB_HOST=localhost
DB_PORT=5432
DB_USERNAME=loan_user
DB_PASSWORD=loan_password

# JWT (shared across services)
JWT_SECRET=oneday-migration-secret-key
JWT_EXPIRATION=86400000

# Service URLs (for inter-service communication)
AUTH_SERVICE_URL=http://localhost:8081
LOAN_SERVICE_URL=http://localhost:8082
SUPPORT_SERVICE_URL=http://localhost:8083
```
LOG_LEVEL=INFO
```

### Service-Specific Databases
```bash
# IAM Service
IAM_DB_NAME=iam_db
IAM_DB_USERNAME=iam_user
IAM_DB_PASSWORD=iam_password

# Loan Product Service
LOAN_PRODUCT_DB_NAME=loan_product_db
LOAN_PRODUCT_DB_USERNAME=loan_product_user
LOAN_PRODUCT_DB_PASSWORD=loan_product_password

# Loan Application Service
LOAN_APP_DB_NAME=loan_application_db
LOAN_APP_DB_USERNAME=loan_app_user
LOAN_APP_DB_PASSWORD=loan_app_password

# Document Service
DOCUMENT_DB_NAME=document_db
DOCUMENT_DB_USERNAME=document_user
DOCUMENT_DB_PASSWORD=document_password

# Disbursement Service
DISBURSEMENT_DB_NAME=disbursement_db
DISBURSEMENT_DB_USERNAME=disbursement_user
DISBURSEMENT_DB_PASSWORD=disbursement_password

# Notification Service
NOTIFICATION_DB_NAME=notification_db
NOTIFICATION_DB_USERNAME=notification_user
NOTIFICATION_DB_PASSWORD=notification_password

# Reporting Service
REPORTING_DB_NAME=reporting_db
REPORTING_DB_USERNAME=reporting_user
REPORTING_DB_PASSWORD=reporting_password
```

## Docker Commands

### Build all services
```bash
#!/bin/bash
services=("eureka-server" "config-server" "api-gateway" "iam-service" "loan-product-service" "loan-application-service" "document-service" "disbursement-service" "notification-service" "reporting-service")

for service in "${services[@]}"; do
    echo "Building $service..."
    cd $service
    ./mvnw clean package -DskipTests
    docker build -t loan-management/$service:latest .
    cd ..
done
```

### Start infrastructure services
```bash
docker-compose up -d zookeeper kafka redis postgres eureka-server config-server
```

### Start all microservices
```bash
docker-compose up -d
```

### View logs
```bash
# View logs for all services
docker-compose logs -f

# View logs for specific service
docker-compose logs -f iam-service
```

### Stop all services
```bash
docker-compose down
```

## Kafka Topics

| Topic | Purpose | Producer | Consumer |
|-------|---------|----------|----------|
| user-events | User lifecycle events | IAM Service | All services |
| loan-application-events | Application status changes | Loan App Service | Notification, Disbursement |
| document-events | Document upload/validation | Document Service | Loan App Service |
| disbursement-events | Payment transactions | Disbursement Service | Notification, Reporting |
| notification-events | Notification delivery status | Notification Service | Reporting |

## Monitoring URLs

| Service | Metrics | Health | Dashboard |
|---------|---------|--------|-----------|
| Prometheus | http://localhost:9090 | - | Metrics Collection |
| Grafana | http://localhost:3000 | - | Dashboards |
| Kibana | http://localhost:5601 | - | Log Analysis |
| Eureka | http://localhost:8761 | - | Service Registry |
| Zipkin | http://localhost:9411 | - | Distributed Tracing |

## Common Commands

### Check service registration
```bash
curl http://localhost:8761/eureka/apps
```

### Test authentication
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"password"}'
```

### Health check all services
```bash
for port in 8080 8081 8082 8083 8084 8085 8086 8087 8888 8761; do
  echo "Checking port $port..."
  curl -s http://localhost:$port/actuator/health | jq .status
done
```

### View Kafka topics
```bash
## Quick Development Commands

### Start all services locally
```bash
# Terminal 1: Authentication Service
cd authentication-service
mvn spring-boot:run

# Terminal 2: Loan Service  
cd loan-service
mvn spring-boot:run

# Terminal 3: Support Service
cd support-service
mvn spring-boot:run
```

### Quick health checks
```bash
# Check all services are running
curl http://localhost:8081/actuator/health
curl http://localhost:8082/actuator/health  
curl http://localhost:8083/actuator/health
```

### Test basic functionality
```bash
# Test authentication
curl -X POST http://localhost:8081/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"password"}'

# Test loan service (with JWT token)
curl -X GET http://localhost:8082/loan-products \
  -H "Authorization: Bearer <your-jwt-token>"
```

## One-Day Troubleshooting

### Common Issues & Quick Fixes

#### Service Won't Start
1. Check port conflicts: `netstat -ano | findstr :8081`
2. Verify Java version: `java -version` (should be 21)
3. Check database connection in application.yml
4. Look at startup logs for specific errors

#### Database Connection Issues
1. **H2 Database**: Should work out of the box
2. **PostgreSQL**: Verify service is running and credentials are correct
3. Check application.yml database configuration
4. Try connecting with database client first

#### JWT Token Issues
1. Ensure same secret key across all services
2. Check token expiration time is reasonable
3. Verify token format (should start with "Bearer ")
4. Test token generation in Auth Service first

#### Inter-Service Communication
1. Verify services are running on correct ports
2. Check network connectivity between services
3. Use simple HTTP calls initially (avoid complex patterns)
4. Test with Postman before integrating frontend

### Emergency Rollback Steps
1. Stop all microservices: `Ctrl+C` in terminals
2. Start original monolith: `mvn spring-boot:run` in original project
3. Restore database from backup if needed
4. Verify monolith is working properly

## Quick Reference Links

- **H2 Console**: http://localhost:808X/h2-console (if H2 is used)
- **Actuator Health**: http://localhost:808X/actuator/health
- **API Testing**: Use Postman or curl commands above
- **Logs**: Check console output for each service terminal
- [ ] Sensitive configuration is externalized
- [ ] Security headers are properly set
- [ ] Input validation is implemented
- [ ] SQL injection prevention is in place
- [ ] XSS protection is enabled

## Contact Information

| Role | Contact | Responsibility |
|------|---------|----------------|
| Architecture Lead | architect@company.com | Overall design decisions |
| DevOps Engineer | devops@company.com | Infrastructure and deployment |
| Security Engineer | security@company.com | Security reviews and compliance |
| Database Administrator | dba@company.com | Database migration and optimization |
| QA Lead | qa@company.com | Testing strategy and execution |
| Product Owner | po@company.com | Business requirements and priorities |

## Emergency Contacts

**On-Call Engineer**: +1-XXX-XXX-XXXX
**Emergency Escalation**: emergency@company.com
**Incident Management**: incidents@company.com
