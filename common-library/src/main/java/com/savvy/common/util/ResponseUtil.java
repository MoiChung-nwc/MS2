package com.savvy.common.util;

import com.savvy.common.dto.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseUtil {

    private ResponseUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static <T> ResponseEntity<BaseResponse<T>> ok(T data) {
        return ResponseEntity.ok(BaseResponse.success(data));
    }

    public static <T> ResponseEntity<BaseResponse<T>> ok(T data, String message) {
        return ResponseEntity.ok(BaseResponse.success(data, message));
    }

    public static <T> ResponseEntity<BaseResponse<T>> created(T data, String message) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.created(data, message));
    }

    public static ResponseEntity<BaseResponse<?>> noContent() {
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(BaseResponse.success(null, "Operation completed successfully", HttpStatus.NO_CONTENT));
    }

    public static ResponseEntity<BaseResponse<?>> error(String code, String message, HttpStatus status) {
        return ResponseEntity.status(status)
                .body(BaseResponse.error(code, message, status));
    }

    public static ResponseEntity<BaseResponse<?>> badRequest(String message) {
        return error("BAD_REQUEST", message, HttpStatus.BAD_REQUEST);
    }

    public static ResponseEntity<BaseResponse<?>> notFound(String message) {
        return error("NOT_FOUND", message, HttpStatus.NOT_FOUND);
    }

    public static ResponseEntity<BaseResponse<?>> unauthorized(String message) {
        return error("UNAUTHORIZED", message, HttpStatus.UNAUTHORIZED);
    }

    public static ResponseEntity<BaseResponse<?>> forbidden(String message) {
        return error("FORBIDDEN", message, HttpStatus.FORBIDDEN);
    }
}
