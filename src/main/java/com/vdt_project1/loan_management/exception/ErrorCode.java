package com.vdt_project1.loan_management.exception;

import jakarta.validation.constraints.*;
import lombok.Getter;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
        UNCATEGORIZED_ERROR(9999, "An uncategorized error occurred", HttpStatusCode.valueOf(500)),
        SERVER_ERROR(9998, "An internal server error occurred", HttpStatusCode.valueOf(500)),
        INVALID_MESSAGE_KEY(1111, "Invalid message key", HttpStatusCode.valueOf(400)),
        USER_EXISTS(1001, "User already exists", HttpStatusCode.valueOf(400)),
        INVALID_EMAIL(1002, "Email must be in a valid format", HttpStatusCode.valueOf(400)),
        INVALID_PASSWORD(1003, "Password must be at least 6 characters long", HttpStatusCode.valueOf(400)),
        INVALID_STATUS(1003, "Status must be either 'ACTIVE' or 'INACTIVE'", HttpStatusCode.valueOf(400)),
        USER_NOT_EXIST(1004, "User does not exist", HttpStatusCode.valueOf(404)),
        UNAUTHENTICATED(1005, "User is not authenticated", HttpStatusCode.valueOf(401)),
        UNAUTHORIZED(1006, "Unauthorized access", HttpStatusCode.valueOf(403)),
        USER_ACCOUNT_INACTIVE(1007, "User account is inactive", HttpStatusCode.valueOf(403)),
        INVALID_DOB(1007, "Your age must be at least {min}", HttpStatusCode.valueOf(400)),
        ROLE_NOT_EXIST(1008, "Role does not exist", HttpStatusCode.valueOf(404)),
        LOAN_PRODUCT_NOT_FOUND(2001, "Loan product not found", HttpStatusCode.valueOf(404)),
        INVALID_LOAN_PRODUCT_AMOUNT_RANGE(2002, "Minimum loan amount must be less than maximum loan amount",
                        HttpStatusCode.valueOf(400)),
        INVALID_LOAN_PRODUCT_TERM_RANGE(2003, "Minimum loan term must be less than maximum loan term",
                        HttpStatusCode.valueOf(400)),
        INVALID_LOAN_PRODUCT_DOCUMENTS(2004, "At least one required document must be specified",
                        HttpStatusCode.valueOf(400)),
        INVALID_LOAN_PRODUCT_MIN_INTEREST_RATE(2006, "Interest rate must be between {min} and {max}",
                        HttpStatusCode.valueOf(400)),
        INVALID_LOAN_PRODUCT_MAX_INTEREST_RATE(2005, "Interest rate must be between {min} and {max}",
                        HttpStatusCode.valueOf(400)),
        INVALID_LOAN_PRODUCT_NAME(2007, "Loan product name must be at least {min} characters long",
                        HttpStatusCode.valueOf(400)),
        INVALID_LOAN_PRODUCT_MIN_AMOUNT(2008, "Minimum amount must be at least {min}",
                        HttpStatusCode.valueOf(400)),
        INVALID_LOAN_PRODUCT_MAX_AMOUNT(2009, "Maximum amount must be at least {min}",
                        HttpStatusCode.valueOf(400)),
        INVALID_LOAN_PRODUCT_MIN_TERM(2010, "Minimum term must be at least {min}",
                        HttpStatusCode.valueOf(400)),
        INVALID_LOAN_PRODUCT_MAX_TERM(2011, "Maximum term must be at least {min}",
                        HttpStatusCode.valueOf(400)),
        LOAN_APPLICATION_NOT_FOUND(3001, "Loan application not found",
                        HttpStatusCode.valueOf(404)),
        INVALID_LOAN_APPLICATION_AMOUNT(3002, "Requested amount must be between {min} and {max}",
                        HttpStatusCode.valueOf(400)),
        INVALID_LOAN_APPLICATION_TERM(3003, "Requested term must be between {min} and {max}",
                        HttpStatusCode.valueOf(400)),
        INVALID_LOAN_APPLICATION_PERSONAL_INFO(3004, "Personal information must be at least {min} characters long",
                        HttpStatusCode.valueOf(400)),
        LOAN_APPLICATION_ALREADY_REJECTED(3005, "Loan application has already been rejected",
                        HttpStatusCode.valueOf(400)),
        NOTIFICATION_NOT_FOUND(4001, "Notification not found", HttpStatusCode.valueOf(404)),
        DOCUMENT_NOT_FOUND(5001, "Document not found", HttpStatusCode.valueOf(404)),
        DISBURSEMENT_NOT_FOUND(7001, "Disbursement transaction not found", HttpStatusCode.valueOf(404)),
        LOAN_APPLICATION_NOT_APPROVED(7002, "Loan application must be approved before disbursement",
                        HttpStatusCode.valueOf(400)),
        DISBURSEMENT_AMOUNT_EXCEEDS_APPROVED(7003, "Total disbursement amount cannot exceed approved loan amount",
                        HttpStatusCode.valueOf(400)),
        VERIFICATION_TOKEN_NOT_FOUND(6001, "Verification token not found", HttpStatusCode.valueOf(404)),
        VERIFICATION_TOKEN_ALREADY_VERIFIED(6002, "Verification token has already been verified",
                        HttpStatusCode.valueOf(400)),
        INVALID_VERIFICATION_TOKEN_TYPE(6003, "Invalid verification token type", HttpStatusCode.valueOf(400)),
        VERIFICATION_TOKEN_EXPIRED(6004, "Verification token has expired", HttpStatusCode.valueOf(400));

        private int code;
        private String message;
        private HttpStatusCode httpStatusCode;

        ErrorCode(int code, String message, HttpStatusCode httpStatusCode) {
                this.code = code;
                this.message = message;
                this.httpStatusCode = httpStatusCode;
        }
}
