package com.savvycom.auth_service.admin.controller;

import com.savvy.common.dto.BaseResponse;
import com.savvycom.auth_service.admin.dto.request.CreateRoleRequest;
import com.savvycom.auth_service.admin.dto.request.UpdateRoleRequest;
import com.savvycom.auth_service.admin.dto.response.RoleResponse;
import com.savvycom.auth_service.admin.service.AdminRoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/roles")
public class AdminRoleController {

    private final AdminRoleService adminRoleService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<List<RoleResponse>>> listRoles() {
        return ok("OK", adminRoleService.listRoles());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<RoleResponse>> createRole(
            @Valid @RequestBody CreateRoleRequest request
    ) {
        return ok("Role created", adminRoleService.createRole(request));
    }

    @PutMapping("/{roleId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<RoleResponse>> updateRole(
            @PathVariable Long roleId,
            @Valid @RequestBody UpdateRoleRequest request
    ) {
        return ok("Role updated", adminRoleService.updateRole(roleId, request));
    }

    @DeleteMapping("/{roleId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<Void>> deleteRole(@PathVariable Long roleId) {
        adminRoleService.deleteRole(roleId);
        return ok("Role deleted", null);
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
