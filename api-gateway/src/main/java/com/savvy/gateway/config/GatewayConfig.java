//package com.savvy.gateway.config;
//
//import org.springframework.cloud.gateway.route.RouteLocator;
//import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class GatewayConfig {
//    public static final String[] PUBLIC_URL = {"/api/v1/auth/**", "/api/courses/**"};
//    public static final String[] Require_Auth = {"/api/profile/**"};
//
//    public static final String[] ADMIN_URL = {"/api/v1/admin/**"};
//
//    public static final String[] INSTRUCTOR_URL = {"/api/v1/instructor/**"};
//    public static final String[] GARDE_URL = {"/api/v1/grades/**"};
//    public static final String[] STUDENT_URL = {"/api/v1/student/**"};
//    @Bean
//    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
//        return builder.routes()
//                // Auth Service Routes
//                .route("auth-service", r -> r
//                        .path(PUBLIC_URL)
//                        .filters(f -> f
//                                .addRequestHeader("X-Gateway", "API-Gateway")
//                                .addResponseHeader("X-Response-Time", String.valueOf(System.currentTimeMillis())))
//                        .uri("lb://auth-service"))
//
//                // Student Service Routes
//                .route("student-service", r -> r
//                        .path(STUDENT_URL)
//                        .filters(f -> f
//                                .addRequestHeader("X-Gateway", "API-Gateway")
//                                .addResponseHeader("X-Response-Time", String.valueOf(System.currentTimeMillis())))
//                        .uri("lb://student-service"))
//
//                // Grade Service Routes
//                .route("grade-service", r -> r
//                        .path(GARDE_URL)
//                        .filters(f -> f
//                                .addRequestHeader("X-Gateway", "API-Gateway")
//                                .addResponseHeader("X-Response-Time", String.valueOf(System.currentTimeMillis())))
//                        .uri("lb://grade-service"))
//
//                .build();
//    }
//}
