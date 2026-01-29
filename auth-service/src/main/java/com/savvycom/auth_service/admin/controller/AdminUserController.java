package com.savvycom.auth_service.admin.controller;

import com.savvy.common.dto.BaseResponse;
import com.savvy.common.dto.PageResponse;
import com.savvycom.auth_service.admin.dto.request.AdminCreateUserRequest;
import com.savvycom.auth_service.admin.dto.request.AdminSetUserRolesRequest;
import com.savvycom.auth_service.admin.dto.request.AdminUpdateUserRequest;
import com.savvycom.auth_service.admin.dto.response.AdminUserDetailResponse;
import com.savvycom.auth_service.admin.dto.response.AdminUserSummaryResponse;
import com.savvycom.auth_service.admin.service.AdminUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/users")
public class AdminUserController {

    private final AdminUserService adminUserService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<PageResponse<AdminUserSummaryResponse>>> listUsers(Pageable pageable) {
        return ok("OK", adminUserService.listUsers(pageable));
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<AdminUserDetailResponse>> getUser(@PathVariable Long userId) {
        return ok("OK", adminUserService.getUser(userId));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<AdminUserDetailResponse>> createUser(
            @Valid @RequestBody AdminCreateUserRequest request
    ) {
        return ok("User created", adminUserService.createUser(request));
    }

    @PutMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<AdminUserDetailResponse>> updateUser(
            @PathVariable Long userId,
            @Valid @RequestBody AdminUpdateUserRequest request
    ) {
        return ok("User updated", adminUserService.updateUser(userId, request));
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<Void>> deleteUser(@PathVariable Long userId) {
        adminUserService.deleteUser(userId);
        return ok("User deleted", null);
    }

    @PutMapping("/{userId}/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<AdminUserDetailResponse>> setUserRoles(
            @PathVariable Long userId,
            @Valid @RequestBody AdminSetUserRolesRequest request
    ) {
        return ok("User roles updated", adminUserService.setUserRoles(userId, request));
    }

    private <T> ResponseEntity<BaseResponse<T>> ok(String message, T data) {
        return ResponseEntity.ok(BaseResponse.<T>builder()
                .success(true)
                .status(200)
                .message(message)
                .data(data)
                .build());
    }
}
