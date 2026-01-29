package com.savvy.common.exception;

import com.savvy.common.dto.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<BaseResponse<?>> handleBusinessException(
            BusinessException ex, WebRequest request) {
        log.error("Business exception: {} - {}", ex.getErrorCode().getCode(), ex.getMessage(), ex);

        BaseResponse<?> response = BaseResponse.error(
                ex.getErrorCode().getCode(),
                ex.getEffectiveMessage(),
                ex.getErrorCode().getHttpStatus(),
                request.getDescription(false)
        );

        return ResponseEntity
                .status(ex.getErrorCode().getHttpStatus())
                .body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<?>> handleValidationException(
            MethodArgumentNotValidException ex, WebRequest request) {
        log.error("Validation exception: {}", ex.getMessage());

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        String errorDetails = errors.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .collect(Collectors.joining(", "));

        BaseResponse<?> response = BaseResponse.error(
                ErrorCode.VALIDATION_ERROR.getCode(),
                "Validation failed",
                HttpStatus.BAD_REQUEST,
                errorDetails
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<BaseResponse<?>> handleTypeMismatchException(
            MethodArgumentTypeMismatchException ex, WebRequest request) {
        log.error("Type mismatch exception: {}", ex.getMessage());

        String message = String.format("Invalid value '%s' for parameter '%s'",
                ex.getValue(), ex.getName());

        BaseResponse<?> response = BaseResponse.error(
                ErrorCode.INVALID_INPUT.getCode(),
                message,
                HttpStatus.BAD_REQUEST,
                ex.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<BaseResponse<?>> handleIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request) {
        log.error("Illegal argument exception: {}", ex.getMessage(), ex);

        BaseResponse<?> response = BaseResponse.error(
                ErrorCode.INVALID_INPUT.getCode(),
                ex.getMessage(),
                HttpStatus.BAD_REQUEST,
                request.getDescription(false)
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<?>> handleGlobalException(
            Exception ex, WebRequest request) {
        log.error("Unexpected exception: {}", ex.getMessage(), ex);

        BaseResponse<?> response = BaseResponse.error(
                ErrorCode.INTERNAL_SERVER_ERROR.getCode(),
                "An unexpected error occurred",
                HttpStatus.INTERNAL_SERVER_ERROR,
                ex.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }
}
