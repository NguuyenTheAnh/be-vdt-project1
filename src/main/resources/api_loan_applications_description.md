# API Loan Applications Description

This document provides a detailed description of the APIs available in the `LoanApplicationController`.

## Base Path: `/loan-applications`

---

### 1. Create Loan Application
- **Endpoint:** `POST /`
- **Description:** Creates a new loan application.
- **Permissions Required:** `POST_LOAN_APPLICATIONS_CREATE` or `ADMIN` role.
- **Request Body:** `LoanApplicationRequest`
  ```json
  {
    "productId": Long, // ID of the loan product being applied for (Required)
    "requestedAmount": Long, // Amount requested by the user (Required, Min: 1)
    "requestedTerm": Integer, // Loan term in months (Required, Min: 1)    "personalInfo": "String", // Personal information of the applicant (Required, Min length: 10)
    "status": "NEW", // Optional: Initial status of the application (e.g., NEW, PENDING). Defaults to NEW if not provided.
                     // Possible values: NEW, PENDING, REQUIRE_MORE_INFO, APPROVED, REJECTED, DISBURSED
    "internalNotes": "String" // Optional: Internal notes for the application
  }
  ```
- **Response:** `ApiResponse<LoanApplicationResponse>`
  ```json
  {
    "code": 1000,
    "message": null,
    "data": {
      "id": Long,
      "requestedAmount": Long,
      "requestedTerm": Integer,      "personalInfo": "String",
      "status": "String", // e.g., NEW, PENDING
      "internalNotes": "String",
      "createdAt": "LocalDateTime", // (ISO 8601 format)
      "updatedAt": "LocalDateTime", // (ISO 8601 format)
      "loanProduct": { // Details of the associated loan product
        "id": Long,
        "name": "String",
        "description": "String",
        "interestRate": Double,
        "minAmount": Long,
        "maxAmount": Long,
        "minTerm": Integer,
        "maxTerm": Integer,
        "status": "String", // e.g., ACTIVE, INACTIVE
        "requiredDocuments": "String", // Space-separated list of required document types
        "createdAt": "LocalDateTime",
        "updatedAt": "LocalDateTime"
      },
      "user": { // Details of the user who created the application
        "id": Long,
        "email": "String",
        "fullName": "String",
        "phoneNumber": "String",
        "address": "String",
        "accountStatus": "String", // e.g., ACTIVE, INACTIVE
        "createdAt": "LocalDateTime",
        "updatedAt": "LocalDateTime",
        "role": {
          "name": "String",
          "description": "String",
          "permissions": [
            {
              "name": "String",
              "description": "String"
            }
          ]
        }
      }
    }
  }
  ```

---

### 2. Get All Loan Applications
- **Endpoint:** `GET /`
- **Description:** Retrieves a paginated list of all loan applications.
- **Permissions Required:** `GET_LOAN_APPLICATIONS_ALL` or `ADMIN` role.
- **Query Parameters:**
    - `page`: Integer (optional) - Page number (0-indexed).
    - `size`: Integer (optional) - Number of items per page.
    - `sort`: String (optional) - Sorting criteria (e.g., `createdAt,desc`).
- **Response:** `ApiResponse<Page<LoanApplicationResponse>>`
  - The `data` field will contain a paginated list of `LoanApplicationResponse` objects (see structure in API 1).

---

### 3. Get Required Documents for a Loan Product (related to an Application)
- **Endpoint:** `GET /required-documents/{id}`
- **Description:** Fetches the list of required documents for a specific loan product associated with a loan application. The response is a map where keys are document types and values are the filenames of uploaded documents (or null if not yet uploaded).
- **Permissions Required:** `GET_REQUIRED_DOCUMENTS_BY_LOAN_PRODUCT_ID` or `ADMIN` role.
- **Path Variable:**
    - `id`: Long - The ID of the Loan Application (which is linked to a Loan Product).
- **Response:** `ApiResponse<Map<String, Object>>`
  ```json
  {
    "code": 1000,
    "message": null,
    "data": {
      "ID_PROOF": "id_document.pdf", // Example: Document type "ID_PROOF" with uploaded file "id_document.pdf"
      "INCOME_PROOF": null,        // Example: Document type "INCOME_PROOF" not yet uploaded
      "ADDRESS_PROOF": "address.png"
      // ... other required documents
    }
  }
  ```

---

### 4. Get Loan Applications for Current User
- **Endpoint:** `GET /user`
- **Description:** Retrieves a paginated list of loan applications submitted by the currently authenticated user.
- **Permissions Required:** `GET_LOAN_APPLICATIONS_CURRENT_USER_ALL` or `ADMIN` role.
- **Query Parameters:**
    - `page`: Integer (optional) - Page number (0-indexed).
    - `size`: Integer (optional) - Number of items per page.
    - `sort`: String (optional) - Sorting criteria (e.g., `createdAt,desc`).
- **Response:** `ApiResponse<Page<LoanApplicationResponse>>`
  - The `data` field will contain a paginated list of `LoanApplicationResponse` objects (see structure in API 1).

---

### 5. Get Loan Application by ID
- **Endpoint:** `GET /{id}`
- **Description:** Retrieves a specific loan application by its ID.
- **Permissions Required:** `GET_LOAN_APPLICATIONS_BY_ID` or `ADMIN` role.
- **Path Variable:**
    - `id`: Long - The ID of the loan application.
- **Response:** `ApiResponse<LoanApplicationResponse>` (see structure in API 1).

---

### 6. Update Loan Application
- **Endpoint:** `PATCH /{id}`
- **Description:** Updates an existing loan application.
- **Permissions Required:** `PATCH_LOAN_APPLICATIONS_UPDATE_BY_ID` or `ADMIN` role.
- **Path Variable:**
    - `id`: Long - The ID of the loan application to update.
- **Request Body:** `LoanApplicationRequest` (see structure in API 1, fields are optional for update)
- **Response:** `ApiResponse<LoanApplicationResponse>` (see structure in API 1 with updated fields).

---

### 7. Update Loan Application Status
- **Endpoint:** `PATCH /{id}/status`
- **Description:** Updates the status of a specific loan application.
- **Permissions Required:** `PATCH_LOAN_APPLICATIONS_UPDATE_STATUS_BY_ID` or `ADMIN` role.
- **Path Variable:**
    - `id`: Long - The ID of the loan application.
- **Query Parameter:**
    - `status`: String (Required) - The new status for the loan application.
      Possible values: `NEW`, `PENDING`, `REQUIRE_MORE_INFO`, `APPROVED`, `REJECTED`, `DISBURSED`.
- **Response:** `ApiResponse<LoanApplicationResponse>` (see structure in API 1 with updated status).

---

### 8. Delete Loan Application by ID
- **Endpoint:** `DELETE /{id}`
- **Description:** Deletes a specific loan application by its ID.
- **Permissions Required:** `DELETE_LOAN_APPLICATIONS_BY_ID` or `ADMIN` role.
- **Path Variable:**
    - `id`: Long - The ID of the loan application to delete.
- **Response:** `ApiResponse<Void>`
  ```json
  {
    "code": 1000,
    "message": null, // Or a success message
    "data": null
  }
  ```

---

## Common Response Wrapper `ApiResponse<T>`

All API responses are wrapped in an `ApiResponse` object:

```json
{
  "code": Integer, // Status code (e.g., 1000 for success, other codes for errors)
  "message": "String", // Optional message, usually for errors
  "data": T // The actual response data, type varies by endpoint
}
```

## Enum: `LoanApplicationStatus`
Used in `LoanApplicationRequest` (optional for creation, used in status update) and `LoanApplicationResponse`.
- `NEW`
- `PENDING`
- `REQUIRE_MORE_INFO`
- `APPROVED`
- `REJECTED`
- `DISBURSED`
