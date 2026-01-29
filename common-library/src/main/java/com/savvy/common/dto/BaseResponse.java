package com.savvy.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseResponse<T> {
    private boolean success;
    private int status;
    private String message;

    @JsonInclude(JsonInclude.Include.ALWAYS)
    private T data;

    private LocalDateTime timestamp;
    private ErrorDetail error;

    @Data
    @Builder
    public static class ErrorDetail {
        private String code;
        private String message;
        private String details;
    }

    public static <T> BaseResponse<T> success(T data) {
        return success(data, "Success", HttpStatus.OK);
    }

    public static <T> BaseResponse<T> success(T data, String message) {
        return success(data, message, HttpStatus.OK);
    }

    public static <T> BaseResponse<T> success(T data, String message, HttpStatus status) {
        return BaseResponse.<T>builder()
                .success(true)
                .status(status.value())
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> BaseResponse<T> created(T data, String message) {
        return success(data, message, HttpStatus.CREATED);
    }

    public static BaseResponse<?> error(String code, String message) {
        return error(code, message, HttpStatus.INTERNAL_SERVER_ERROR, null);
    }

    public static BaseResponse<?> error(String code, String message, HttpStatus status) {
        return error(code, message, status, null);
    }

    public static BaseResponse<?> error(String code, String message, HttpStatus status, String details) {
        return BaseResponse.builder()
                .success(false)
                .status(status.value())
                .message(message)
                .error(ErrorDetail.builder()
                        .code(code)
                        .message(message)
                        .details(details)
                        .build())
                .timestamp(LocalDateTime.now())
                .build();
    }
}
