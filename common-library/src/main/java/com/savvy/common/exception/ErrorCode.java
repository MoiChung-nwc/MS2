package com.savvy.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // Common errors (1xxx)
    INTERNAL_SERVER_ERROR("1000", "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_INPUT("1001", "Invalid input data", HttpStatus.BAD_REQUEST),
    RESOURCE_NOT_FOUND("1002", "Resource not found", HttpStatus.NOT_FOUND),
    UNAUTHORIZED("1003", "Unauthorized access", HttpStatus.UNAUTHORIZED),
    FORBIDDEN("1004", "Access forbidden", HttpStatus.FORBIDDEN),
    METHOD_NOT_ALLOWED("1005", "Method not allowed", HttpStatus.METHOD_NOT_ALLOWED),
    VALIDATION_ERROR("1006", "Validation failed", HttpStatus.BAD_REQUEST),
    DUPLICATE_RESOURCE("1007", "Resource already exists", HttpStatus.CONFLICT),

    // Authentication errors (2xxx)
    INVALID_CREDENTIALS("2001", "Invalid username or password", HttpStatus.UNAUTHORIZED),
    TOKEN_EXPIRED("2002", "Authentication token has expired", HttpStatus.UNAUTHORIZED),
    TOKEN_INVALID("2003", "Invalid authentication token", HttpStatus.UNAUTHORIZED),
    ACCOUNT_LOCKED("2004", "Account is locked", HttpStatus.FORBIDDEN),
    ACCOUNT_DISABLED("2005", "Account is disabled", HttpStatus.FORBIDDEN),

    // Student service errors (3xxx)
    STUDENT_NOT_FOUND("3001", "Student not found", HttpStatus.NOT_FOUND),
    STUDENT_ALREADY_EXISTS("3002", "Student already exists", HttpStatus.CONFLICT),
    INVALID_STUDENT_DATA("3003", "Invalid student data", HttpStatus.BAD_REQUEST),

    // Grade service errors (4xxx)
    GRADE_NOT_FOUND("4001", "Grade not found", HttpStatus.NOT_FOUND),
    INVALID_GRADE_VALUE("4002", "Invalid grade value", HttpStatus.BAD_REQUEST),
    GRADE_ALREADY_EXISTS("4003", "Grade already exists for this student", HttpStatus.CONFLICT),

    // Business logic errors (5xxx)
    OPERATION_NOT_ALLOWED("5001", "Operation not allowed", HttpStatus.BAD_REQUEST),
    INSUFFICIENT_PERMISSIONS("5002", "Insufficient permissions", HttpStatus.FORBIDDEN),
    QUOTA_EXCEEDED("5003", "Quota exceeded", HttpStatus.TOO_MANY_REQUESTS),

    // External service errors (6xxx)
    SERVICE_UNAVAILABLE("6001", "Service temporarily unavailable", HttpStatus.SERVICE_UNAVAILABLE),
    GATEWAY_TIMEOUT("6002", "Gateway timeout", HttpStatus.GATEWAY_TIMEOUT),
    EXTERNAL_SERVICE_ERROR("6003", "External service error", HttpStatus.BAD_GATEWAY);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(String code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
