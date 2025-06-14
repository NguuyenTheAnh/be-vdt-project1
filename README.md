# Hệ thống quản lý cho vay (Loan Management System - VDT 2025)

## Giới thiệu

Hệ thống quản lý cho vay là một ứng dụng web RESTful API được phát triển bằng Spring Boot, cung cấp giải pháp toàn diện cho việc quản lý quy trình cho vay từ đăng ký, xét duyệt, đến giải ngân và theo dõi. Hệ thống hỗ trợ quản lý người dùng, sản phẩm cho vay, hồ sơ vay, tài liệu, thông báo và báo cáo thống kê.

## Tính năng chính

### Quản lý xác thực và người dùng
- Đăng ký tài khoản với xác thực email
- Đăng nhập/đăng xuất với JWT token
- Phân quyền người dùng (USER, ADMIN)
- Quản lý hồ sơ cá nhân
- Đặt lại mật khẩu qua email
- Quản lý trạng thái tài khoản

### Quản lý sản phẩm cho vay
- Tạo và quản lý các gói sản phẩm cho vay
- Cấu hình lãi suất, hạn mức, thời hạn cho vay
- Thiết lập danh sách tài liệu yêu cầu
- Kích hoạt/tạm ngưng sản phẩm
- Thống kê theo sản phẩm

### Quy trình đăng ký và xét duyệt vay
- Nộp đơn xin vay trực tuyến
- Upload và quản lý tài liệu hỗ trợ (PDF, DOC, hình ảnh)
- Theo dõi trạng thái đơn vay realtime
- Xét duyệt đơn vay với ghi chú nội bộ
- Yêu cầu bổ sung thông tin từ khách hàng
- Thông báo tự động qua email và hệ thống

### Hệ thống giải ngân
- Giải ngân từng phần hoặc toàn bộ
- Theo dõi lịch sử giao dịch giải ngân
- Tự động cập nhật trạng thái đơn vay
- Gửi thông báo giải ngân qua email
- Báo cáo tổng hợp giải ngân

### Hệ thống thông báo
- Thông báo realtime trong hệ thống
- Gửi email tự động cho các sự kiện quan trọng
- Quản lý trạng thái đã đọc/chưa đọc
- Template email có thể tùy chỉnh

### Báo cáo và thống kê
- Thống kê đơn vay theo trạng thái
- Báo cáo tỷ lệ duyệt/từ chối
- Thống kê theo sản phẩm cho vay
- Báo cáo số tiền giải ngân theo thời gian
- Dashboard tổng quan cho admin

## Công nghệ sử dụng

### Backend Framework
- **Java 21** - Ngôn ngữ lập trình chính
- **Spring Boot 3.5.0** - Framework chính
- **Spring Security** - Xác thực và phân quyền
- **Spring Data JPA** - Thao tác cơ sở dữ liệu
- **Spring Web** - RESTful API
- **Spring Validation** - Validation dữ liệu
- **OAuth2 Resource Server** - JWT token handling

### Cơ sở dữ liệu
- **PostgreSQL** - Cơ sở dữ liệu chính
- **JPA/Hibernate** - ORM framework

### Thư viện hỗ trợ
- **Lombok** - Giảm boilerplate code
- **MapStruct** - Object mapping
- **Spring Mail** - Gửi email
- **Thymeleaf** - Template engine cho email

### Build Tool
- **Maven** - Quản lý dependencies và build

## Kiến trúc hệ thống

Hệ thống được thiết kế theo mô hình **Layered Architecture**:

```
┌─────────────────┐
│  Controller     │ ← REST API endpoints
├─────────────────┤
│  Service        │ ← Business logic
├─────────────────┤
│  Repository     │ ← Data access layer
├─────────────────┤
│  Entity         │ ← Domain models
└─────────────────┘
```

### Các thành phần chính:
- **Controller Layer**: Xử lý HTTP requests/responses và validation
- **Service Layer**: Xử lý business logic và orchestration
- **Repository Layer**: Thao tác với cơ sở dữ liệu
- **Entity Layer**: Domain models và database mapping
- **DTO Layer**: Data transfer objects cho API
- **Mapper Layer**: Chuyển đổi giữa Entity và DTO

## Cấu trúc dự án

```
src/main/java/com/vdt_project1/loan_management/
├── controller/          # REST API endpoints
│   ├── AuthController.java
│   ├── UserController.java
│   ├── LoanProductController.java
│   ├── LoanApplicationController.java
│   ├── DocumentController.java
│   ├── DisbursementController.java
│   ├── NotificationController.java
│   └── ReportController.java
├── service/            # Business logic services
│   ├── AuthenticationService.java
│   ├── UserService.java
│   ├── LoanProductService.java
│   ├── LoanApplicationService.java
│   ├── DocumentService.java
│   ├── DisbursementTransactionService.java
│   ├── NotificationService.java
│   ├── EmailService.java
│   └── FileService.java
├── repository/         # Data access layer
├── entity/             # JPA entities
│   ├── User.java
│   ├── Role.java
│   ├── LoanProduct.java
│   ├── LoanApplication.java
│   ├── Document.java
│   ├── DisbursementTransaction.java
│   └── Notification.java
├── dto/                # Data transfer objects
│   ├── request/        # Request DTOs
│   └── response/       # Response DTOs
├── mapper/             # MapStruct mappers
├── configuration/      # Spring configurations
│   ├── SecurityConfig.java
│   ├── WebConfig.java
│   └── UploadProperties.java
├── exception/          # Exception handling
├── enums/              # Enumerations
│   ├── LoanApplicationStatus.java
│   ├── LoanProductStatus.java
│   ├── AccountStatus.java
│   └── NotificationType.java
└── validator/          # Custom validators

src/main/resources/
├── templates/          # Email templates
│   ├── email-template.html
│   ├── application-result-template.html
│   └── disburse-template.html
├── application.yml     # Application configuration
└── application-prod.yml # Production configuration
```

## API Documentation

### Authentication APIs
- `POST /api/auth/register` - Đăng ký tài khoản
- `POST /api/auth/login` - Đăng nhập
- `POST /api/auth/refresh` - Refresh JWT token
- `POST /api/auth/logout` - Đăng xuất
- `POST /api/auth/verify-email` - Xác thực email
- `POST /api/auth/forgot-password` - Quên mật khẩu
- `POST /api/auth/reset-password` - Đặt lại mật khẩu

### User Management APIs
- `GET /api/users` - Danh sách người dùng (Admin)
- `GET /api/users/profile` - Thông tin cá nhân
- `PUT /api/users/profile` - Cập nhật thông tin cá nhân
- `PATCH /api/users/{id}/status` - Thay đổi trạng thái tài khoản (Admin)

### Loan Product APIs
- `GET /api/loan-products` - Danh sách sản phẩm cho vay
- `POST /api/loan-products` - Tạo sản phẩm mới (Admin)
- `GET /api/loan-products/{id}` - Chi tiết sản phẩm
- `PUT /api/loan-products/{id}` - Cập nhật sản phẩm (Admin)
- `DELETE /api/loan-products/{id}` - Xóa sản phẩm (Admin)

### Loan Application APIs
- `POST /api/loan-applications` - Tạo đơn vay mới
- `GET /api/loan-applications` - Danh sách đơn vay (Admin)
- `GET /api/loan-applications/user` - Đơn vay của người dùng hiện tại
- `GET /api/loan-applications/{id}` - Chi tiết đơn vay
- `PATCH /api/loan-applications/{id}` - Cập nhật đơn vay
- `PUT /api/loan-applications/{id}/status` - Cập nhật trạng thái (Admin)
- `GET /api/loan-applications/required-documents/{id}` - Tài liệu yêu cầu

### Document Management APIs
- `POST /api/documents` - Upload tài liệu
- `GET /api/documents` - Danh sách tài liệu
- `GET /api/documents/{id}` - Chi tiết tài liệu
- `PUT /api/documents/{id}` - Cập nhật tài liệu
- `DELETE /api/documents/{id}` - Xóa tài liệu

### Disbursement APIs
- `POST /api/disbursements` - Tạo giao dịch giải ngân (Admin)
- `GET /api/disbursements` - Danh sách giao dịch (Admin)
- `GET /api/disbursements/my` - Giao dịch của người dùng
- `GET /api/disbursements/application/{id}` - Giao dịch theo đơn vay
- `GET /api/disbursements/application/{id}/summary` - Tóm tắt giải ngân
- `DELETE /api/disbursements/{id}` - Xóa giao dịch (Admin)

### Notification APIs
- `GET /api/notifications` - Danh sách thông báo
- `POST /api/notifications` - Tạo thông báo
- `PATCH /api/notifications/{id}/read` - Đánh dấu đã đọc
- `GET /api/notifications/unread-count` - Số thông báo chưa đọc

### Report APIs (Admin only)
- `GET /api/reports/applications/by-status` - Thống kê theo trạng thái
- `GET /api/reports/applications/by-product` - Thống kê theo sản phẩm
- `GET /api/reports/applications/approval-ratio` - Tỷ lệ duyệt/từ chối
- `GET /api/reports/disbursements/by-time` - Giải ngân theo thời gian
- `GET /api/reports/dashboard/summary` - Tổng quan dashboard
