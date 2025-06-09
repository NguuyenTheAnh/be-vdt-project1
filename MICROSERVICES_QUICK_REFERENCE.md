# Microservices Migration Quick Reference

## Service Architecture Overview

```
┌─────────────────────────────────────────────────────────────────┐
│                        API Gateway (Port 8080)                  │
│                    Spring Cloud Gateway                         │
└─────────────────────┬───────────────────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────────────────────┐
│                    Service Registry                             │
│                    Eureka Server (Port 8761)                   │
└─────────────────────────────────────────────────────────────────┘

┌──────────────────┐  ┌──────────────────┐  ┌──────────────────┐
│   IAM Service    │  │ Loan Product     │  │ Loan Application │
│   Port: 8081     │  │ Service          │  │ Service          │
│                  │  │ Port: 8082       │  │ Port: 8083       │
└──────────────────┘  └──────────────────┘  └──────────────────┘

┌──────────────────┐  ┌──────────────────┐  ┌──────────────────┐
│ Document Service │  │ Disbursement     │  │ Notification     │
│ Port: 8084       │  │ Service          │  │ Service          │
│                  │  │ Port: 8085       │  │ Port: 8086       │
└──────────────────┘  └──────────────────┘  └──────────────────┘

┌──────────────────┐  ┌──────────────────┐
│ Reporting Service│  │ Config Server    │
│ Port: 8087       │  │ Port: 8888       │
└──────────────────┘  └──────────────────┘
```

## Service Responsibilities

| Service | Responsibility | Key Entities | Database |
|---------|---------------|--------------|----------|
| **IAM Service** | Authentication, Authorization, User Management | User, Role, Permission | iam_db |
| **Loan Product Service** | Product catalog, configurations, eligibility | LoanProduct, InterestRate, EligibilityCriteria | loan_product_db |
| **Loan Application Service** | Application lifecycle, approval workflow | LoanApplication, ApplicationStatus | loan_application_db |
| **Document Service** | File management, document validation | Document, DocumentType | document_db |
| **Disbursement Service** | Payment processing, transaction management | DisbursementTransaction, PaymentSchedule | disbursement_db |
| **Notification Service** | Multi-channel notifications | Notification, NotificationTemplate | notification_db |
| **Reporting Service** | Business reports, analytics | Report, ReportConfiguration | reporting_db |

## Port Assignments

| Service | Port | Health Check URL |
|---------|------|------------------|
| API Gateway | 8080 | http://localhost:8080/actuator/health |
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
GET    /api/disbursements/{id}      - Get disbursement
PUT    /api/disbursements/{id}      - Update disbursement
POST   /api/disbursements/{id}/process - Process disbursement
GET    /api/disbursements/{id}/status  - Get status
```

### Notifications (Notification Service)
```
GET    /api/notifications           - List notifications
POST   /api/notifications/send      - Send notification
GET    /api/notifications/{id}      - Get notification
PUT    /api/notifications/{id}      - Update notification
GET    /api/notifications/templates - List templates
POST   /api/notifications/templates - Create template
```

### Reports (Reporting Service)
```
GET    /api/reports                 - List reports
POST   /api/reports/generate        - Generate report
GET    /api/reports/{id}            - Get report
GET    /api/analytics/dashboard     - Dashboard data
GET    /api/analytics/metrics       - System metrics
```

## Environment Variables

### Common Environment Variables
```bash
# Database Configuration
DB_HOST=localhost
DB_PORT=5432
DB_USERNAME=your_username
DB_PASSWORD=your_password

# JWT Configuration
JWT_SECRET=your-jwt-secret-key
JWT_EXPIRATION=86400000

# Kafka Configuration
KAFKA_BOOTSTRAP_SERVERS=localhost:9092

# Eureka Configuration
EUREKA_SERVER_URL=http://localhost:8761/eureka/

# Redis Configuration (for API Gateway)
REDIS_HOST=localhost
REDIS_PORT=6379

# Environment
ENVIRONMENT=development
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
docker exec -it kafka kafka-topics --bootstrap-server localhost:9092 --list
```

### Check database connections
```bash
# IAM Database
psql -h localhost -U iam_user -d iam_db -c "SELECT COUNT(*) FROM users;"

# Loan Product Database
psql -h localhost -U loan_product_user -d loan_product_db -c "SELECT COUNT(*) FROM loan_products;"
```

## Troubleshooting

### Service Discovery Issues
1. Check Eureka server is running: `curl http://localhost:8761`
2. Verify service registration in Eureka dashboard
3. Check service configuration: `eureka.client.service-url.defaultZone`

### Database Connection Issues
1. Verify database is running and accessible
2. Check connection string format
3. Validate username/password
4. Check firewall rules

### Authentication Issues
1. Verify JWT secret key consistency across services
2. Check token expiration settings
3. Validate user credentials in IAM database
4. Check API Gateway authentication filter

### Performance Issues
1. Check application metrics in Grafana
2. Monitor database query performance
3. Review service logs for errors
4. Analyze network latency between services

### Message Queue Issues
1. Verify Kafka is running: `docker-compose ps kafka`
2. Check topic creation: `kafka-topics --list`
3. Monitor consumer lag
4. Review message serialization/deserialization

## Security Checklist

- [ ] All inter-service communication uses HTTPS
- [ ] JWT tokens have appropriate expiration times
- [ ] Database connections use encrypted passwords
- [ ] API Gateway has rate limiting enabled
- [ ] Service-to-service authentication is configured
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
