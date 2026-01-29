package com.savvycom.auth_service.admin.helper;

import com.savvy.common.exception.BusinessException;
import com.savvy.common.exception.ErrorCode;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Locale;

@Component
public class AdminUserValidator {

    public String normalizeEmailRequired(String email) {
        if (!StringUtils.hasText(email)) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "Email is required");
        }
        return email.trim().toLowerCase(Locale.ROOT);
    }

    public String normalizeUsernameRequired(String username) {
        if (!StringUtils.hasText(username)) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "Username is required");
        }
        return username.trim();
    }
}
