package com.savvycom.auth_service.service;

import com.savvycom.auth_service.dto.request.LoginRequest;
import com.savvycom.auth_service.dto.request.LogoutRequest;
import com.savvycom.auth_service.dto.request.RefreshRequest;
import com.savvycom.auth_service.dto.request.RegisterRequest;
import com.savvycom.auth_service.dto.response.RegisterResponse;
import com.savvycom.auth_service.dto.response.TokenResponse;

public interface AuthService {
    RegisterResponse register(RegisterRequest request);
    TokenResponse login(LoginRequest request);
    TokenResponse refresh(RefreshRequest request);
    void logout(LogoutRequest request);
}
