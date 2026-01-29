package com.savvy.common.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final ErrorCode errorCode;
    private final String customMessage;
    private final Object[] args;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.customMessage = null;
        this.args = null;
    }

    public BusinessException(ErrorCode errorCode, String customMessage) {
        super(customMessage != null ? customMessage : errorCode.getMessage());
        this.errorCode = errorCode;
        this.customMessage = customMessage;
        this.args = null;
    }

    public BusinessException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
        this.customMessage = null;
        this.args = null;
    }

    public BusinessException(ErrorCode errorCode, String customMessage, Throwable cause) {
        super(customMessage != null ? customMessage : errorCode.getMessage(), cause);
        this.errorCode = errorCode;
        this.customMessage = customMessage;
        this.args = null;
    }

    public BusinessException(ErrorCode errorCode, String customMessage, Object... args) {
        super(customMessage != null ? customMessage : errorCode.getMessage());
        this.errorCode = errorCode;
        this.customMessage = customMessage;
        this.args = args;
    }

    public String getEffectiveMessage() {
        return customMessage != null ? customMessage : errorCode.getMessage();
    }
}
