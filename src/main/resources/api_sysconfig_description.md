# System Configuration API Documentation

## Overview
The System Configuration API provides complete CRUD operations for managing system-wide configuration settings. This API allows administrators to dynamically configure application behavior, feature flags, and system parameters without requiring code changes or application restarts.

## Main Features
- **Create Configuration**: Add new configuration keys with values
- **Read Configurations**: Retrieve all configurations or by ID/key
- **Update Configuration**: Modify existing configuration values
- **Delete Configuration**: Remove configuration entries
- **Unique Key Validation**: Ensures configuration keys are unique across the system

## Base Path
`/system-configurations`

## Authentication & Authorization
All endpoints require authentication and **ADMIN** role authorization. Users with insufficient permissions will receive a `403 Forbidden` response.

## Response Format
All endpoints return responses in the standard `ApiResponse` format:
```json
{
  "code": 1000,
  "message": "Success message",
  "data": {...}
}
```

## Configuration Key Guidelines
- **Naming Convention**: Use descriptive, hierarchical keys (e.g., `email.smtp.host`, `security.password.min_length`)
- **Maximum Length**: 100 characters
- **Uniqueness**: Each key must be unique across the system
- **Case Sensitivity**: Configuration keys are case-sensitive

---

## Endpoints

### 1. Create System Configuration
**POST** `/system-configurations`

Creates a new system configuration entry with a unique key.

**Authorization:** `ADMIN` role required

**Request:**
- **Method:** POST
- **URL:** `/system-configurations`
- **Headers:** 
  - `Authorization: Bearer {token}`
  - `Content-Type: application/json`

**Request Body:**
```json
{
  "configKey": "email.smtp.host",
  "configValue": "smtp.gmail.com",
  "description": "SMTP server hostname for email sending"
}
```

**Request Fields:**
- `configKey` (String, required): Unique configuration key (max 100 characters)
- `configValue` (String, required): Configuration value (can be any text)
- `description` (String, optional): Human-readable description of the configuration

**Response:**
```json
{
  "code": 1000,
  "message": "System configuration created successfully",
  "data": {
    "configId": 1,
    "configKey": "email.smtp.host",
    "configValue": "smtp.gmail.com",
    "description": "SMTP server hostname for email sending",
    "createdAt": "2024-01-15T10:30:00.123456",
    "updatedAt": "2024-01-15T10:30:00.123456"
  }
}
```

**Error Responses:**
- `400 Bad Request - Config Key Exists`:
  ```json
  {
    "code": 8002,
    "message": "Configuration key already exists",
    "data": null
  }
  ```
- `400 Bad Request - Validation Error`:
  ```json
  {
    "code": 1003,
    "message": "Config key is required",
    "data": null
  }
  ```

**Usage Examples:**
```bash
# Create email configuration
curl -X POST "http://localhost:8080/api/system-configurations" \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{
    "configKey": "email.smtp.port",
    "configValue": "587",
    "description": "SMTP server port"
  }'
```

---

### 2. Get All System Configurations
**GET** `/system-configurations`

Retrieves all system configuration entries.

**Authorization:** `ADMIN` role required

**Request:**
- **Method:** GET
- **URL:** `/system-configurations`
- **Headers:** 
  - `Authorization: Bearer {token}`
  - `Content-Type: application/json`
- **Parameters:** None

**Response:**
```json
{
  "code": 1000,
  "message": "System configurations retrieved successfully",
  "data": [
    {
      "configId": 1,
      "configKey": "email.smtp.host",
      "configValue": "smtp.gmail.com",
      "description": "SMTP server hostname for email sending",
      "createdAt": "2024-01-15T10:30:00.123456",
      "updatedAt": "2024-01-15T10:30:00.123456"
    },
    {
      "configId": 2,
      "configKey": "email.smtp.port",
      "configValue": "587",
      "description": "SMTP server port",
      "createdAt": "2024-01-15T10:31:00.123456",
      "updatedAt": "2024-01-15T10:31:00.123456"
    },
    {
      "configId": 3,
      "configKey": "security.password.min_length",
      "configValue": "8",
      "description": "Minimum password length requirement",
      "createdAt": "2024-01-15T10:32:00.123456",
      "updatedAt": "2024-01-15T10:32:00.123456"
    }
  ]
}
```

**Response Fields:**
- `configId` (Long): Unique identifier for the configuration
- `configKey` (String): The configuration key
- `configValue` (String): The configuration value
- `description` (String): Description of the configuration
- `createdAt` (LocalDateTime): When the configuration was created
- `updatedAt` (LocalDateTime): When the configuration was last updated

**Usage Examples:**
```bash
# Get all configurations
curl -X GET "http://localhost:8080/api/system-configurations" \
  -H "Authorization: Bearer {token}"
```

---

### 3. Get System Configuration by ID
**GET** `/system-configurations/{configId}`

Retrieves a specific system configuration by its ID.

**Authorization:** `ADMIN` role required

**Request:**
- **Method:** GET
- **URL:** `/system-configurations/{configId}`
- **Headers:** 
  - `Authorization: Bearer {token}`
  - `Content-Type: application/json`
- **Path Parameters:**
  - `configId` (Long): The unique ID of the configuration

**Response:**
```json
{
  "code": 1000,
  "message": "System configuration retrieved successfully",
  "data": {
    "configId": 1,
    "configKey": "email.smtp.host",
    "configValue": "smtp.gmail.com",
    "description": "SMTP server hostname for email sending",
    "createdAt": "2024-01-15T10:30:00.123456",
    "updatedAt": "2024-01-15T10:30:00.123456"
  }
}
```

**Error Responses:**
- `404 Not Found`:
  ```json
  {
    "code": 8001,
    "message": "System configuration not found",
    "data": null
  }
  ```

**Usage Examples:**
```bash
# Get configuration by ID
curl -X GET "http://localhost:8080/api/system-configurations/1" \
  -H "Authorization: Bearer {token}"
```

---

### 4. Get System Configuration by Key
**GET** `/system-configurations/key/{configKey}`

Retrieves a specific system configuration by its key.

**Authorization:** `ADMIN` role required

**Request:**
- **Method:** GET
- **URL:** `/system-configurations/key/{configKey}`
- **Headers:** 
  - `Authorization: Bearer {token}`
  - `Content-Type: application/json`
- **Path Parameters:**
  - `configKey` (String): The configuration key to search for

**Response:**
```json
{
  "code": 1000,
  "message": "System configuration retrieved successfully",
  "data": {
    "configId": 1,
    "configKey": "email.smtp.host",
    "configValue": "smtp.gmail.com",
    "description": "SMTP server hostname for email sending",
    "createdAt": "2024-01-15T10:30:00.123456",
    "updatedAt": "2024-01-15T10:30:00.123456"
  }
}
```

**Error Responses:**
- `404 Not Found`:
  ```json
  {
    "code": 8001,
    "message": "System configuration not found",
    "data": null
  }
  ```

**Usage Examples:**
```bash
# Get configuration by key
curl -X GET "http://localhost:8080/api/system-configurations/key/email.smtp.host" \
  -H "Authorization: Bearer {token}"

# Get configuration by key with URL encoding for complex keys
curl -X GET "http://localhost:8080/api/system-configurations/key/security.password.min_length" \
  -H "Authorization: Bearer {token}"
```

---

### 5. Update System Configuration
**PUT** `/system-configurations/{configId}`

Updates an existing system configuration.

**Authorization:** `ADMIN` role required

**Request:**
- **Method:** PUT
- **URL:** `/system-configurations/{configId}`
- **Headers:** 
  - `Authorization: Bearer {token}`
  - `Content-Type: application/json`
- **Path Parameters:**
  - `configId` (Long): The unique ID of the configuration to update

**Request Body:**
```json
{
  "configKey": "email.smtp.host",
  "configValue": "smtp.outlook.com",
  "description": "Updated SMTP server hostname for email sending"
}
```

**Request Fields:**
- `configKey` (String, required): Configuration key (can be changed, but must remain unique)
- `configValue` (String, required): New configuration value
- `description` (String, optional): Updated description

**Response:**
```json
{
  "code": 1000,
  "message": "System configuration updated successfully",
  "data": {
    "configId": 1,
    "configKey": "email.smtp.host",
    "configValue": "smtp.outlook.com",
    "description": "Updated SMTP server hostname for email sending",
    "createdAt": "2024-01-15T10:30:00.123456",
    "updatedAt": "2024-01-15T14:45:00.789012"
  }
}
```

**Business Logic:**
- If the config key is changed, it must not conflict with existing keys
- The `updatedAt` timestamp is automatically updated
- The `createdAt` timestamp remains unchanged

**Error Responses:**
- `404 Not Found`:
  ```json
  {
    "code": 8001,
    "message": "System configuration not found",
    "data": null
  }
  ```
- `400 Bad Request - Key Conflict`:
  ```json
  {
    "code": 8002,
    "message": "Configuration key already exists",
    "data": null
  }
  ```

**Usage Examples:**
```bash
# Update configuration value
curl -X PUT "http://localhost:8080/api/system-configurations/1" \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{
    "configKey": "email.smtp.host",
    "configValue": "smtp.outlook.com",
    "description": "Changed to Outlook SMTP server"
  }'
```

---

### 6. Delete System Configuration
**DELETE** `/system-configurations/{configId}`

Deletes a system configuration entry.

**Authorization:** `ADMIN` role required

**Request:**
- **Method:** DELETE
- **URL:** `/system-configurations/{configId}`
- **Headers:** 
  - `Authorization: Bearer {token}`
  - `Content-Type: application/json`
- **Path Parameters:**
  - `configId` (Long): The unique ID of the configuration to delete

**Response:**
```json
{
  "code": 1000,
  "message": "System configuration deleted successfully",
  "data": null
}
```

**Error Responses:**
- `404 Not Found`:
  ```json
  {
    "code": 8001,
    "message": "System configuration not found",
    "data": null
  }
  ```

**Usage Examples:**
```bash
# Delete configuration
curl -X DELETE "http://localhost:8080/api/system-configurations/1" \
  -H "Authorization: Bearer {token}"
```

---

## Common Configuration Examples

### Email Settings
```json
{
  "configKey": "email.smtp.host",
  "configValue": "smtp.gmail.com",
  "description": "SMTP server hostname"
}
{
  "configKey": "email.smtp.port",
  "configValue": "587",
  "description": "SMTP server port"
}
{
  "configKey": "email.smtp.username",
  "configValue": "noreply@company.com",
  "description": "SMTP authentication username"
}
```

### Security Settings
```json
{
  "configKey": "security.password.min_length",
  "configValue": "8",
  "description": "Minimum password length"
}
{
  "configKey": "security.session.timeout",
  "configValue": "1800",
  "description": "Session timeout in seconds"
}
{
  "configKey": "security.max_login_attempts",
  "configValue": "5",
  "description": "Maximum failed login attempts before lockout"
}
```

### Business Settings
```json
{
  "configKey": "loan.max_amount",
  "configValue": "10000000",
  "description": "Maximum loan amount in VND"
}
{
  "configKey": "loan.default_interest_rate",
  "configValue": "12.5",
  "description": "Default annual interest rate percentage"
}
{
  "configKey": "system.maintenance_mode",
  "configValue": "false",
  "description": "Enable/disable maintenance mode"
}
```

---

## Error Handling

### Common Error Codes

**403 Forbidden - Insufficient Permissions:**
```json
{
  "code": 1009,
  "message": "You do not have permission",
  "data": null
}
```

**400 Bad Request - Validation Error:**
```json
{
  "code": 1003,
  "message": "Config key is required",
  "data": null
}
```

**8001 - Configuration Not Found:**
```json
{
  "code": 8001,
  "message": "System configuration not found",
  "data": null
}
```

**8002 - Configuration Key Exists:**
```json
{
  "code": 8002,
  "message": "Configuration key already exists",
  "data": null
}
```

**500 Internal Server Error:**
```json
{
  "code": 9999,
  "message": "Internal server error occurred",
  "data": null
}
```

---

## Data Types & Validation

### Request Validation Rules
- **configKey**: Required, max 100 characters, unique across system
- **configValue**: Required, no length limit (stored as TEXT)
- **description**: Optional, no length limit (stored as TEXT)

### Response Data Types
- **configId**: Long (auto-generated primary key)
- **configKey**: String (max 100 characters)
- **configValue**: String (unlimited length)
- **description**: String (unlimited length, nullable)
- **createdAt**: LocalDateTime (ISO 8601 format)
- **updatedAt**: LocalDateTime (ISO 8601 format)

---

## Best Practices

### Configuration Key Naming
1. **Use hierarchical structure**: `module.component.setting`
2. **Be descriptive**: Avoid abbreviations
3. **Use lowercase with dots**: `email.smtp.host` not `EMAIL_SMTP_HOST`
4. **Group related settings**: All email settings start with `email.`

### Configuration Values
1. **Store as strings**: Even numbers and booleans should be stored as strings
2. **Use standard formats**: For dates use ISO 8601, for booleans use "true"/"false"
3. **Document units**: Include units in description (e.g., "timeout in seconds")
4. **Avoid sensitive data**: Use secure configuration management for passwords/keys

### Usage Patterns
1. **Cache frequently used values**: Implement application-level caching
2. **Validate on retrieval**: Parse and validate values when reading
3. **Default values**: Always have fallback defaults in application code
4. **Change notifications**: Consider implementing change listeners for dynamic updates

---

## Security Considerations

- **Admin Only Access**: All operations require ADMIN role
- **Input Validation**: All inputs are validated for format and length
- **Audit Trail**: Created/updated timestamps provide change tracking
- **No Sensitive Data**: Avoid storing passwords or API keys directly
- **Key Uniqueness**: Enforced at database level to prevent conflicts

---

## Integration Examples

### Frontend Configuration Management
```javascript
// Fetch all configurations
async function getAllConfigurations() {
  const response = await fetch('/api/system-configurations', {
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    }
  });
  return response.json();
}

// Get specific configuration by key
async function getConfigByKey(key) {
  const response = await fetch(`/system-configurations/key/${key}`, {
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    }
  });
  return response.json();
}

// Update configuration
async function updateConfig(configId, data) {
  const response = await fetch(`/system-configurations/${configId}`, {
    method: 'PUT',
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(data)
  });
  return response.json();
}
```

### Backend Service Integration
```java
// Example service to read configuration values
@Service
public class ConfigurationService {
    
    @Autowired
    private SystemConfigurationRepository configRepo;
    
    public String getConfigValue(String key, String defaultValue) {
        return configRepo.findByConfigKey(key)
            .map(SystemConfiguration::getConfigValue)
            .orElse(defaultValue);
    }
    
    public Integer getIntConfig(String key, Integer defaultValue) {
        try {
            String value = getConfigValue(key, null);
            return value != null ? Integer.parseInt(value) : defaultValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    public Boolean getBooleanConfig(String key, Boolean defaultValue) {
        String value = getConfigValue(key, null);
        return value != null ? Boolean.parseBoolean(value) : defaultValue;
    }
}
```

---

## Performance Considerations

- **Database Indexing**: The `config_key` field is indexed for fast lookups
- **Caching Strategy**: Consider implementing Redis or in-memory caching for frequently accessed configurations
- **Batch Operations**: For bulk configuration updates, consider implementing batch endpoints
- **Connection Pooling**: Ensure proper database connection pooling for concurrent access

---

## Change Log

### Version 1.0.0
- Initial implementation of CRUD operations for system configurations
- Unique key validation and conflict prevention
- Comprehensive error handling and validation
- ADMIN role authorization for all operations
- Automatic timestamp management (created_at, updated_at)
- Support for unlimited text values and descriptions

---

## Special Protected Configurations

Hệ thống có một số config đặc biệt được đánh dấu là **PROTECTED** - nghĩa là chúng không thể bị xóa mà chỉ có thể được đọc và cập nhật. Đây là những config quan trọng cho hoạt động cốt lõi của hệ thống.

### 1. Required Documents Configuration
**Config Key:** `required_documents`

Config này quản lý danh sách các loại tài liệu có thể yêu cầu trong hệ thống. Giá trị được lưu trữ dưới dạng JSON object với key-value mapping.

**Cấu trúc dữ liệu:**
```json
{
  "configKey": "required_documents",
  "configValue": "{\"ID_CARD\":\"CMND/CCCD\",\"PROOF_OF_INCOME\":\"Chứng minh thu nhập\",\"PROOF_OF_ADDRESS\":\"Chứng minh địa chỉ\",\"EMPLOYMENT_VERIFICATION\":\"Xác nhận việc làm\",\"BANK_STATEMENT\":\"Sao kê ngân hàng\",\"COLLATERAL_DOCUMENT\":\"Tài liệu tài sản đảm bảo\",\"BUSINESS_LICENSE\":\"Giấy phép kinh doanh\",\"TAX_RETURN\":\"Tờ khai thuế\",\"INSURANCE_POLICY\":\"Hợp đồng bảo hiểm\",\"PROPERTY_OWNERSHIP\":\"Giấy tờ sở hữu tài sản\",\"MARRIAGE_CERTIFICATE\":\"Giấy chứng nhận kết hôn\",\"BIRTH_CERTIFICATE\":\"Giấy khai sinh\",\"UTILITY_BILL\":\"Hóa đơn tiện ích\",\"VEHICLE_REGISTRATION\":\"Đăng ký phương tiện\",\"TRAVEL_ITINERARY\":\"Lịch trình du lịch\",\"FISHING_LICENSE\":\"Giấy phép đánh bắt cá\",\"IMPORT_CONTRACT\":\"Hợp đồng nhập khẩu\",\"ADMISSION_LETTER\":\"Thư nhập học\",\"BUSINESS_PLAN\":\"Kế hoạch kinh doanh\",\"DRIVING_LICENSE\":\"Giấy phép lái xe\",\"EQUIPMENT_PURCHASE_CONTRACT\":\"Hợp đồng mua thiết bị\",\"ENVIRONMENTAL_PERMIT\":\"Giấy phép môi trường\",\"EXPORT_CONTRACT\":\"Hợp đồng xuất khẩu\",\"FARMING_PLAN\":\"Kế hoạch canh tác\",\"FINANCIAL_STATEMENT\":\"Báo cáo tài chính\",\"LAND_OWNERSHIP_CERTIFICATE\":\"Giấy chứng nhận quyền sử dụng đất\",\"LAND_USE_RIGHTS\":\"Giấy quyền sử dụng đất\",\"MEDICAL_INVOICE\":\"Hóa đơn y tế\",\"PROPERTY_DOCUMENT\":\"Giấy tờ tài sản\",\"SALARY_STATEMENT\":\"Bảng lương\",\"STUDENT_ID\":\"Thẻ sinh viên\",\"STUDENT_VISA\":\"Visa du học\",\"TUITION_INVOICE\":\"Hóa đơn học phí\",\"VEHICLE_CONTRACT\":\"Hợp đồng mua xe\",\"VEHICLE_PURCHASE_CONTRACT\":\"Hợp đồng mua phương tiện\"}",
  "description": "Danh sách các loại tài liệu có thể yêu cầu cho đơn vay - Key-Value mapping của document types"
}
```

**Cấu trúc JSON trong configValue (sau khi parse):**
```javascript
{
  // Tài liệu cá nhân cơ bản
  "ID_CARD": "CMND/CCCD",
  "DRIVING_LICENSE": "Giấy phép lái xe", 
  "BIRTH_CERTIFICATE": "Giấy khai sinh",
  "MARRIAGE_CERTIFICATE": "Giấy chứng nhận kết hôn",
  
  // Tài liệu nghề nghiệp & thu nhập
  "EMPLOYMENT_VERIFICATION": "Xác nhận việc làm",
  "SALARY_STATEMENT": "Bảng lương",
  "PROOF_OF_INCOME": "Chứng minh thu nhập",
  
  // Tài liệu địa chỉ & cư trú
  "PROOF_OF_ADDRESS": "Chứng minh địa chỉ",
  "UTILITY_BILL": "Hóa đơn tiện ích",
  
  // Tài liệu tài sản
  "PROPERTY_OWNERSHIP": "Giấy tờ sở hữu tài sản",
  "PROPERTY_DOCUMENT": "Giấy tờ tài sản",
  "LAND_OWNERSHIP_CERTIFICATE": "Giấy chứng nhận quyền sử dụng đất",
  "LAND_USE_RIGHTS": "Giấy quyền sử dụng đất",
  "COLLATERAL_DOCUMENT": "Tài liệu tài sản đảm bảo",
  
  // Tài liệu kinh doanh
  "BUSINESS_LICENSE": "Giấy phép kinh doanh",
  "BUSINESS_PLAN": "Kế hoạch kinh doanh",
  "FINANCIAL_STATEMENT": "Báo cáo tài chính",
  "TAX_RETURN": "Tờ khai thuế",
  
  // Tài liệu tài chính
  "BANK_STATEMENT": "Sao kê ngân hàng",
  "INSURANCE_POLICY": "Hợp đồng bảo hiểm",
  
  // Tài liệu giáo dục
  "STUDENT_ID": "Thẻ sinh viên",
  "ADMISSION_LETTER": "Thư nhập học",
  "STUDENT_VISA": "Visa du học",
  "TUITION_INVOICE": "Hóa đơn học phí",
  
  // Tài liệu phương tiện
  "VEHICLE_REGISTRATION": "Đăng ký phương tiện",
  "VEHICLE_CONTRACT": "Hợp đồng mua xe",
  "VEHICLE_PURCHASE_CONTRACT": "Hợp đồng mua phương tiện",
  
  // Tài liệu y tế
  "MEDICAL_INVOICE": "Hóa đơn y tế",
  
  // Tài liệu chuyên ngành
  "FISHING_LICENSE": "Giấy phép đánh bắt cá",
  "ENVIRONMENTAL_PERMIT": "Giấy phép môi trường",
  "FARMING_PLAN": "Kế hoạch canh tác",
  "TRAVEL_ITINERARY": "Lịch trình du lịch",
  "IMPORT_CONTRACT": "Hợp đồng nhập khẩu",
  "EXPORT_CONTRACT": "Hợp đồng xuất khẩu",
  "EQUIPMENT_PURCHASE_CONTRACT": "Hợp đồng mua thiết bị"
}
```

### Frontend Integration Guide

#### 📋 **1. Lấy và Parse Dữ liệu**
```javascript
// Fetch required documents configuration
async function getRequiredDocuments() {
  try {
    const response = await fetch('/api/system-configurations/key/required_documents', {
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    });
    
    const result = await response.json();
    
    if (result.code === 1000) {
      // Parse JSON string to object
      const documentsMap = JSON.parse(result.data.configValue);
      return documentsMap;
    } else {
      console.error('Failed to fetch required documents:', result.message);
      return {};
    }
  } catch (error) {
    console.error('Error fetching required documents:', error);
    return {};
  }
}

// Usage example
const documents = await getRequiredDocuments();
console.log(documents); // { "ID_CARD": "CMND/CCCD", ... }
```

#### 🎨 **2. Hiển thị Dropdown/Select Options**
```javascript
// Generate options for document type dropdown
function generateDocumentTypeOptions(documentsMap) {
  const selectElement = document.getElementById('documentTypeSelect');
  
  // Clear existing options
  selectElement.innerHTML = '<option value="">-- Chọn loại tài liệu --</option>';
  
  // Group documents by category for better UX
  const groupedDocs = groupDocumentsByCategory(documentsMap);
  
  Object.entries(groupedDocs).forEach(([category, docs]) => {
    // Create optgroup for each category
    const optgroup = document.createElement('optgroup');
    optgroup.label = category;
    
    docs.forEach(([key, value]) => {
      const option = document.createElement('option');
      option.value = key;
      option.textContent = value;
      optgroup.appendChild(option);
    });
    
    selectElement.appendChild(optgroup);
  });
}

// Group documents by category for better organization
function groupDocumentsByCategory(documentsMap) {
  const categories = {
    '📋 Tài liệu cá nhân': [
      'ID_CARD', 'DRIVING_LICENSE', 'BIRTH_CERTIFICATE', 'MARRIAGE_CERTIFICATE'
    ],
    '💼 Tài liệu nghề nghiệp': [
      'EMPLOYMENT_VERIFICATION', 'SALARY_STATEMENT', 'PROOF_OF_INCOME'
    ],
    '🏠 Tài liệu địa chỉ & cư trú': [
      'PROOF_OF_ADDRESS', 'UTILITY_BILL'
    ],
    '🏢 Tài liệu tài sản': [
      'PROPERTY_OWNERSHIP', 'PROPERTY_DOCUMENT', 'LAND_OWNERSHIP_CERTIFICATE', 
      'LAND_USE_RIGHTS', 'COLLATERAL_DOCUMENT'
    ],
    '📊 Tài liệu kinh doanh': [
      'BUSINESS_LICENSE', 'BUSINESS_PLAN', 'FINANCIAL_STATEMENT', 'TAX_RETURN'
    ],
    '💳 Tài liệu tài chính': [
      'BANK_STATEMENT', 'INSURANCE_POLICY'
    ],
    '🎓 Tài liệu giáo dục': [
      'STUDENT_ID', 'ADMISSION_LETTER', 'STUDENT_VISA', 'TUITION_INVOICE'
    ],
    '🚗 Tài liệu phương tiện': [
      'VEHICLE_REGISTRATION', 'VEHICLE_CONTRACT', 'VEHICLE_PURCHASE_CONTRACT'
    ],
    '🏥 Tài liệu y tế': [
      'MEDICAL_INVOICE'
    ],
    '🎯 Tài liệu chuyên ngành': [
      'FISHING_LICENSE', 'ENVIRONMENTAL_PERMIT', 'FARMING_PLAN', 
      'TRAVEL_ITINERARY', 'IMPORT_CONTRACT', 'EXPORT_CONTRACT', 
      'EQUIPMENT_PURCHASE_CONTRACT'
    ]
  };
  
  const grouped = {};
  
  Object.entries(categories).forEach(([categoryName, documentKeys]) => {
    grouped[categoryName] = documentKeys
      .filter(key => documentsMap[key]) // Only include existing documents
      .map(key => [key, documentsMap[key]]);
  });
  
  return grouped;
}
```

#### 📱 **3. Hiển thị Danh Sách với Search/Filter**
```javascript
// Create searchable document list
function createDocumentList(documentsMap) {
  const container = document.getElementById('documentListContainer');
  
  // Create search input
  const searchInput = document.createElement('input');
  searchInput.type = 'text';
  searchInput.placeholder = 'Tìm kiếm loại tài liệu...';
  searchInput.className = 'form-control mb-3';
  searchInput.addEventListener('input', (e) => filterDocuments(e.target.value, documentsMap));
  
  container.appendChild(searchInput);
  
  // Create document list
  const listContainer = document.createElement('div');
  listContainer.id = 'documentList';
  listContainer.className = 'document-list';
  
  renderDocumentList(documentsMap, listContainer);
  container.appendChild(listContainer);
}

function renderDocumentList(documentsMap, container) {
  container.innerHTML = '';
  
  const groupedDocs = groupDocumentsByCategory(documentsMap);
  
  Object.entries(groupedDocs).forEach(([category, docs]) => {
    if (docs.length === 0) return;
    
    // Category header
    const categoryHeader = document.createElement('h5');
    categoryHeader.className = 'mt-4 mb-2 text-primary';
    categoryHeader.textContent = category;
    container.appendChild(categoryHeader);
    
    // Document cards
    const cardContainer = document.createElement('div');
    cardContainer.className = 'row';
    
    docs.forEach(([key, value]) => {
      const card = createDocumentCard(key, value);
      cardContainer.appendChild(card);
    });
    
    container.appendChild(cardContainer);
  });
}

function createDocumentCard(key, value) {
  const cardDiv = document.createElement('div');
  cardDiv.className = 'col-md-6 col-lg-4 mb-3';
  
  cardDiv.innerHTML = `
    <div class="card h-100 document-card" data-key="${key}" data-value="${value}">
      <div class="card-body">
        <h6 class="card-title">${value}</h6>
        <p class="card-text">
          <small class="text-muted">Mã: ${key}</small>
        </p>
        <div class="card-actions">
          <button class="btn btn-sm btn-primary" onclick="selectDocument('${key}', '${value}')">
            Chọn
          </button>
          <button class="btn btn-sm btn-outline-info" onclick="showDocumentInfo('${key}', '${value}')">
            Chi tiết
          </button>
        </div>
      </div>
    </div>
  `;
  
  return cardDiv;
}

// Filter documents based on search term
function filterDocuments(searchTerm, documentsMap) {
  const filteredDocs = {};
  const term = searchTerm.toLowerCase();
  
  Object.entries(documentsMap).forEach(([key, value]) => {
    if (key.toLowerCase().includes(term) || 
        value.toLowerCase().includes(term)) {
      filteredDocs[key] = value;
    }
  });
  
  const container = document.getElementById('documentList');
  renderDocumentList(filteredDocs, container);
}
```

#### 🔧 **4. Utility Functions**
```javascript
// Get document display name by key
function getDocumentDisplayName(documentKey, documentsMap) {
  return documentsMap[documentKey] || documentKey;
}

// Get all document keys
function getAllDocumentKeys(documentsMap) {
  return Object.keys(documentsMap);
}

// Get documents by category
function getDocumentsByCategory(category, documentsMap) {
  const categoryMapping = {
    'personal': ['ID_CARD', 'DRIVING_LICENSE', 'BIRTH_CERTIFICATE', 'MARRIAGE_CERTIFICATE'],
    'professional': ['EMPLOYMENT_VERIFICATION', 'SALARY_STATEMENT', 'PROOF_OF_INCOME'],
    'address': ['PROOF_OF_ADDRESS', 'UTILITY_BILL'],
    'property': ['PROPERTY_OWNERSHIP', 'PROPERTY_DOCUMENT', 'LAND_OWNERSHIP_CERTIFICATE', 'LAND_USE_RIGHTS', 'COLLATERAL_DOCUMENT'],
    'business': ['BUSINESS_LICENSE', 'BUSINESS_PLAN', 'FINANCIAL_STATEMENT', 'TAX_RETURN'],
    'financial': ['BANK_STATEMENT', 'INSURANCE_POLICY'],
    'education': ['STUDENT_ID', 'ADMISSION_LETTER', 'STUDENT_VISA', 'TUITION_INVOICE'],
    'vehicle': ['VEHICLE_REGISTRATION', 'VEHICLE_CONTRACT', 'VEHICLE_PURCHASE_CONTRACT'],
    'medical': ['MEDICAL_INVOICE'],
    'specialized': ['FISHING_LICENSE', 'ENVIRONMENTAL_PERMIT', 'FARMING_PLAN', 'TRAVEL_ITINERARY', 'IMPORT_CONTRACT', 'EXPORT_CONTRACT', 'EQUIPMENT_PURCHASE_CONTRACT']
  };
  
  const keys = categoryMapping[category] || [];
  const result = {};
  
  keys.forEach(key => {
    if (documentsMap[key]) {
      result[key] = documentsMap[key];
    }
  });
  
  return result;
}

// Validate document key exists
function isValidDocumentKey(documentKey, documentsMap) {
  return documentsMap.hasOwnProperty(documentKey);
}

// Convert to array format for easier iteration
function documentsToArray(documentsMap) {
  return Object.entries(documentsMap).map(([key, value]) => ({
    key,
    value,
    displayName: value
  }));
}
```

#### 🎨 **5. CSS Styling (Optional)**
```css
.document-card {
  transition: transform 0.2s, box-shadow 0.2s;
  cursor: pointer;
}

.document-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0,0,0,0.1);
}

.document-list {
  max-height: 600px;
  overflow-y: auto;
}

.card-actions {
  margin-top: auto;
}

.card-actions .btn {
  margin-right: 5px;
}

optgroup {
  font-weight: bold;
  color: #495057;
}

optgroup option {
  font-weight: normal;
  padding-left: 20px;
}
```

#### 📊 **6. Thống Kê và Quản Lý**
```javascript
// Get document statistics
function getDocumentStatistics(documentsMap) {
  const categories = groupDocumentsByCategory(documentsMap);
  
  return {
    total: Object.keys(documentsMap).length,
    byCategory: Object.entries(categories).map(([name, docs]) => ({
      category: name.replace(/📋|💼|🏠|🏢|📊|💳|🎓|🚗|🏥|🎯/g, '').trim(),
      count: docs.length,
      documents: docs
    }))
  };
}

// Export documents to different formats
function exportDocuments(documentsMap, format = 'json') {
  switch (format) {
    case 'json':
      return JSON.stringify(documentsMap, null, 2);
    
    case 'csv':
      const array = documentsToArray(documentsMap);
      const csv = ['Key,Display Name']
        .concat(array.map(doc => `${doc.key},"${doc.value}"`))
        .join('\n');
      return csv;
    
    case 'table':
      const stats = getDocumentStatistics(documentsMap);
      console.table(stats.byCategory);
      return stats;
    
    default:
      return documentsMap;
  }
}
```

### Ví dụ sử dụng hoàn chỉnh:

```javascript
// Initialize document management
async function initializeDocuments() {
  try {
    // 1. Fetch documents configuration
    const documentsMap = await getRequiredDocuments();
    
    // 2. Create dropdown for document selection
    generateDocumentTypeOptions(documentsMap);
    
    // 3. Create searchable list
    createDocumentList(documentsMap);
    
    // 4. Get statistics
    const stats = getDocumentStatistics(documentsMap);
    console.log('Document Statistics:', stats);
    
    // 5. Set up event handlers
    setupDocumentEventHandlers(documentsMap);
    
  } catch (error) {
    console.error('Failed to initialize documents:', error);
  }
}

// Call when page loads
document.addEventListener('DOMContentLoaded', initializeDocuments);
```

### Lưu ý quan trọng:

1. **Caching**: Nên cache dữ liệu documents trong localStorage/sessionStorage để tránh gọi API nhiều lần
2. **Error Handling**: Luôn có fallback khi không lấy được config
3. **Validation**: Validate document key trước khi sử dụng
4. **Performance**: Với danh sách lớn, nên implement pagination hoặc virtual scrolling
5. **Accessibility**: Thêm aria-labels và keyboard navigation support

Cấu trúc này giúp frontend dễ dàng parse, hiển thị và quản lý các loại tài liệu một cách linh hoạt và user-friendly! 🚀
