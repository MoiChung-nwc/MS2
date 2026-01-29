package com.savvycom.auth_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthPingController {

    @GetMapping("/ping")
    public Map<String, Object> ping() {
        return Map.of("service", "auth-service", "status", "UP");
    }
}
