# Microservices Quick Reference Guide

## Architecture Overview

### Service Map
```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   API Gateway   │    │ Service Registry│    │ Config Server   │
│   (Port 8080)   │    │   (Port 8761)   │    │   (Port 8888)   │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                        │                        │
         └────────────────────────┼────────────────────────┘
                                  │
         ┌────────────────────────┼────────────────────────┐
         │                       │                        │
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   IAM Service   │    │ Loan Product    │    │Document Service │
│   (Port 8081)   │    │   (Port 8082)   │    │   (Port 8083)   │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                        │                        │
         └────────────────────────┼────────────────────────┘
                                  │
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│Loan Application │    │ Disbursement    │    │ Notification    │
│   (Port 8084)   │    │   (Port 8085)   │    │   (Port 8086)   │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                        │                        │
         └────────────────────────┼────────────────────────┘
                                  │
         ┌────────────────────────┼────────────────────────┐
         │                       │                        │
┌─────────────────┐    ┌─────────────────┐
│ Reporting       │    │ Configuration   │
│   (Port 8087)   │    │   (Port 8089)   │
└─────────────────┘    └─────────────────┘
```

## Service Details

### 1. IAM Service (Identity & Access Management)
- **Port**: 8081
- **Database**: `iam_db`
- **Purpose**: Authentication, authorization, user management
- **Key Endpoints**:
  - `POST /api/auth/login` - User authentication
  - `POST /api/auth/register` - User registration
  - `GET /api/users/{id}` - Get user details
  - `PUT /api/users/{id}` - Update user
  - `GET /api/roles` - List roles
- **Dependencies**: None (core service)

### 2. Loan Product Service
- **Port**: 8082
- **Database**: `loan_product_db`
- **Purpose**: Manage loan products, terms, and configurations
- **Key Endpoints**:
  - `GET /api/products` - List loan products
  - `POST /api/products` - Create loan product
  - `GET /api/products/{id}` - Get product details
  - `PUT /api/products/{id}` - Update product
  - `DELETE /api/products/{id}` - Delete product
- **Dependencies**: IAM Service

### 3. Document Service
- **Port**: 8083
- **Database**: `document_db`
- **Purpose**: File upload, storage, and management
- **Key Endpoints**:
  - `POST /api/documents/upload` - Upload document
  - `GET /api/documents/{id}` - Download document
  - `GET /api/documents/application/{appId}` - Get documents by application
  - `DELETE /api/documents/{id}` - Delete document
  - `PUT /api/documents/{id}/verify` - Verify document
- **Dependencies**: IAM Service

### 4. Loan Application Service
- **Port**: 8084
- **Database**: `loan_application_db`
- **Purpose**: Handle loan applications and workflows
- **Key Endpoints**:
  - `POST /api/applications` - Submit application
  - `GET /api/applications/{id}` - Get application details
  - `PUT /api/applications/{id}/status` - Update status
  - `GET /api/applications/user/{userId}` - Get user's applications
  - `POST /api/applications/{id}/approve` - Approve application
- **Dependencies**: IAM Service, Loan Product Service, Document Service

### 5. Disbursement Service
- **Port**: 8085
- **Database**: `disbursement_db`
- **Purpose**: Handle loan disbursements and payments
- **Key Endpoints**:
  - `POST /api/disbursements` - Create disbursement
  - `GET /api/disbursements/{id}` - Get disbursement details
  - `PUT /api/disbursements/{id}/status` - Update disbursement status
  - `GET /api/disbursements/loan/{loanId}` - Get loan disbursements
- **Dependencies**: IAM Service, Loan Application Service

### 6. Notification Service
- **Port**: 8086
- **Database**: `notification_db`
- **Purpose**: Send emails, SMS, and push notifications
- **Key Endpoints**:
  - `POST /api/notifications/email` - Send email
  - `POST /api/notifications/sms` - Send SMS
  - `GET /api/notifications/user/{userId}` - Get user notifications
  - `PUT /api/notifications/{id}/read` - Mark as read
- **Dependencies**: IAM Service

### 7. Reporting Service
- **Port**: 8087
- **Database**: `reporting_db`
- **Purpose**: Generate reports and analytics
- **Key Endpoints**:
  - `GET /api/reports/loans` - Loan reports
  - `GET /api/reports/users` - User reports
  - `GET /api/reports/disbursements` - Disbursement reports
  - `POST /api/reports/custom` - Generate custom report
- **Dependencies**: All data services

### 8. Configuration Service
- **Port**: 8089
- **Database**: `config_db`
- **Purpose**: Centralized configuration management
- **Key Endpoints**:
  - `GET /api/config/{service}` - Get service configuration
  - `PUT /api/config/{service}` - Update configuration
  - `GET /api/config/global` - Get global settings
- **Dependencies**: IAM Service

## Infrastructure Services

### API Gateway
- **Port**: 8080
- **Purpose**: Route requests, load balancing, authentication
- **Routes**:
  - `/auth/**` → IAM Service
  - `/products/**` → Loan Product Service
  - `/documents/**` → Document Service
  - `/applications/**` → Loan Application Service
  - `/disbursements/**` → Disbursement Service
  - `/notifications/**` → Notification Service
  - `/reports/**` → Reporting Service
  - `/config/**` → Configuration Service

### Service Registry (Eureka)
- **Port**: 8761
- **Console**: `http://localhost:8761`
- **Purpose**: Service discovery and registration

### Configuration Server
- **Port**: 8888
- **Purpose**: Centralized configuration management
- **Git Repository**: Configuration files stored in Git

## Common Commands

### Docker Commands
```bash
# Build all services
docker-compose build

# Start all services
docker-compose up -d

# Stop all services
docker-compose down

# View logs
docker-compose logs [service-name]

# Scale a service
docker-compose up -d --scale loan-application-service=3
```

### Service Health Checks
```bash
# Check service health
curl http://localhost:8081/actuator/health  # IAM Service
curl http://localhost:8082/actuator/health  # Loan Product Service
curl http://localhost:8083/actuator/health  # Document Service
curl http://localhost:8084/actuator/health  # Loan Application Service
curl http://localhost:8085/actuator/health  # Disbursement Service
curl http://localhost:8086/actuator/health  # Notification Service
curl http://localhost:8087/actuator/health  # Reporting Service
curl http://localhost:8089/actuator/health  # Configuration Service
```

### Service Discovery
```bash
# Check registered services
curl http://localhost:8761/eureka/apps

# Check specific service instances
curl http://localhost:8761/eureka/apps/IAM-SERVICE
```

## Database Schema Quick Reference

### IAM Service (`iam_db`)
```sql
-- Users table
users: id, username, email, password_hash, first_name, last_name, 
       phone_number, status, created_at, updated_at

-- Roles table
roles: id, name, description, permissions, created_at, updated_at

-- User roles junction
user_roles: user_id, role_id, assigned_at
```

### Loan Product Service (`loan_product_db`)
```sql
-- Loan products
loan_products: id, name, description, min_amount, max_amount, 
               interest_rate, term_months, status, created_at, updated_at

-- Product features
product_features: id, product_id, feature_name, feature_value
```

### Document Service (`document_db`)
```sql
-- Documents
documents: id, application_id, document_type, file_name, file_path,
           file_size, mime_type, status, uploaded_by, uploaded_at, verified_at

-- Document types
document_types: id, name, description, required, max_size
```

### Loan Application Service (`loan_application_db`)
```sql
-- Applications
loan_applications: id, user_id, product_id, amount, purpose, status,
                   application_date, approved_date, rejected_date, notes

-- Application status history
application_status_history: id, application_id, previous_status, 
                           new_status, changed_by, changed_at, reason
```

### Disbursement Service (`disbursement_db`)
```sql
-- Disbursements
disbursements: id, application_id, amount, disbursement_date, 
               payment_method, account_details, status, reference_number

-- Payment transactions
payment_transactions: id, disbursement_id, transaction_id, amount,
                      transaction_date, status, gateway_response
```

## Environment Variables

### Common Environment Variables
```bash
# Database
DB_HOST=localhost
DB_PORT=5432
DB_NAME=service_db
DB_USERNAME=postgres
DB_PASSWORD=password

# Service Discovery
EUREKA_SERVER_URL=http://localhost:8761/eureka

# Configuration Server
CONFIG_SERVER_URL=http://localhost:8888

# JWT Configuration
JWT_SECRET=your-secret-key
JWT_EXPIRATION=86400000

# Redis (for caching)
REDIS_HOST=localhost
REDIS_PORT=6379
```

### Service-Specific Variables
```bash
# File Storage (Document Service)
FILE_STORAGE_PATH=/app/uploads
MAX_FILE_SIZE=10MB

# Email Configuration (Notification Service)
SMTP_HOST=smtp.gmail.com
SMTP_PORT=587
SMTP_USERNAME=your-email@gmail.com
SMTP_PASSWORD=your-password

# Payment Gateway (Disbursement Service)
PAYMENT_GATEWAY_URL=https://api.paymentgateway.com
PAYMENT_GATEWAY_API_KEY=your-api-key
```

## Monitoring Endpoints

### Actuator Endpoints (Available on all services)
- `/actuator/health` - Health check
- `/actuator/info` - Service information
- `/actuator/metrics` - Application metrics
- `/actuator/prometheus` - Prometheus metrics
- `/actuator/loggers` - Logger configuration

### Custom Monitoring
- **Prometheus**: `http://localhost:9090`
- **Grafana**: `http://localhost:3000`
- **Zipkin**: `http://localhost:9411`

## Testing

### Integration Test Commands
```bash
# Run all tests
mvn test

# Run specific service tests
mvn test -pl iam-service

# Run integration tests
mvn test -Dtest=*IntegrationTest

# Run performance tests
mvn test -Dtest=*PerformanceTest
```

### API Testing with curl
```bash
# Login
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"password"}'

# Create loan application (with JWT token)
curl -X POST http://localhost:8080/applications \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{"productId":1,"amount":10000,"purpose":"Business expansion"}'
```

## Troubleshooting

### Common Issues

1. **Service not registering with Eureka**
   - Check Eureka server URL in configuration
   - Verify network connectivity
   - Check service name configuration

2. **Database connection issues**
   - Verify database credentials
   - Check database server status
   - Validate connection pool settings

3. **API Gateway routing issues**
   - Check route configuration
   - Verify service registration in Eureka
   - Check load balancer settings

4. **Configuration not loading**
   - Verify Config Server is running
   - Check Git repository access
   - Validate configuration file paths

### Useful Commands for Debugging
```bash
# Check Docker container logs
docker logs container-name

# Check service connectivity
nc -zv localhost 8081

# Monitor service calls
curl http://localhost:8761/eureka/apps | jq .

# Check database connections
docker exec -it postgres psql -U postgres -l
```

## Security Considerations

### JWT Token Management
- Token expiration: 24 hours
- Refresh token mechanism implemented
- Token blacklisting for logout

### API Security
- All endpoints require authentication except `/auth/login` and `/auth/register`
- Role-based access control implemented
- Rate limiting on sensitive endpoints

### Data Protection
- Sensitive data encrypted at rest
- TLS encryption for inter-service communication
- Input validation and sanitization

---

**Note**: This quick reference should be updated as the architecture evolves. Keep it synchronized with actual implementation details.
