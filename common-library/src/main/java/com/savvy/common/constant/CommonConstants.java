package com.savvy.common.constant;

public class CommonConstants {

    private CommonConstants() {
        throw new IllegalStateException("Constants class");
    }

    // Date/Time formats
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String TIME_FORMAT = "HH:mm:ss";

    // Pagination
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int MAX_PAGE_SIZE = 100;
    public static final int DEFAULT_PAGE_NUMBER = 0;

    // API Versioning
    public static final String API_VERSION_V1 = "/api/v1";

    // Headers
    public static final String HEADER_REQUEST_ID = "X-Request-ID";
    public static final String HEADER_USER_ID = "X-User-ID";
    public static final String HEADER_CORRELATION_ID = "X-Correlation-ID";

    // Validation
    public static final int MIN_NAME_LENGTH = 2;
    public static final int MAX_NAME_LENGTH = 100;
    public static final int MIN_PASSWORD_LENGTH = 8;
    public static final int MAX_PASSWORD_LENGTH = 50;

    // Status
    public static final String STATUS_ACTIVE = "ACTIVE";
    public static final String STATUS_INACTIVE = "INACTIVE";
    public static final String STATUS_DELETED = "DELETED";
}
