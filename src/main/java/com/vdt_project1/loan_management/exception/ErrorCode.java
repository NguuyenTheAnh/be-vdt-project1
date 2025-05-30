package com.vdt_project1.loan_management.exception;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_ERROR(9999, "An uncategorized error occurred", HttpStatusCode.valueOf(500)),
    INVALID_MESSAGE_KEY(1111, "Invalid message key", HttpStatusCode.valueOf(400)),
    USER_EXISTS(1001, "User already exists", HttpStatusCode.valueOf(400)),
    INVALID_EMAIL(1002, "Email must be in a valid format", HttpStatusCode.valueOf(400)),
    INVALID_PASSWORD(1003, "Password must be at least 6 characters long", HttpStatusCode.valueOf(400)),
    USER_NOT_EXIST(1004, "User does not exist", HttpStatusCode.valueOf(404)),
    UNAUTHENTICATED(1005, "User is not authenticated", HttpStatusCode.valueOf(401)),
    UNAUTHORIZED(1006, "Unauthorized access", HttpStatusCode.valueOf(403)),
    INVALID_DOB(1007, "Your age must be at least {min}", HttpStatusCode.valueOf(400)),
    ROLE_NOT_EXIST(1008, "Role does not exist", HttpStatusCode.valueOf(404)),
    ;

    private int code;
    private String message;
    private HttpStatusCode httpStatusCode;

    ErrorCode(int code, String message, HttpStatusCode httpStatusCode) {
        this.code = code;
        this.message = message;
        this.httpStatusCode = httpStatusCode;
    }
}
