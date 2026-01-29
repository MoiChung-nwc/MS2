package com.savvycom.auth_service.admin.controller;

import com.savvy.common.dto.BaseResponse;
import com.savvycom.auth_service.admin.dto.request.CreatePermissionRequest;
import com.savvycom.auth_service.admin.dto.request.UpdatePermissionRequest;
import com.savvycom.auth_service.admin.dto.response.PermissionResponse;
import com.savvycom.auth_service.admin.service.AdminPermissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/permissions")
public class AdminPermissionController {

    private final AdminPermissionService adminPermissionService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<List<PermissionResponse>>> listPermissions() {
        return ok("OK", adminPermissionService.listPermissions());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<PermissionResponse>> createPermission(
            @Valid @RequestBody CreatePermissionRequest request
    ) {
        return ok("Permission created", adminPermissionService.createPermission(request));
    }

    @PutMapping("/{permissionId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<PermissionResponse>> updatePermission(
            @PathVariable Long permissionId,
            @Valid @RequestBody UpdatePermissionRequest request
    ) {
        return ok("Permission updated", adminPermissionService.updatePermission(permissionId, request));
    }

    @DeleteMapping("/{permissionId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<Void>> deletePermission(@PathVariable Long permissionId) {
        adminPermissionService.deletePermission(permissionId);
        return ok("Permission deleted", null);
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
