package com.savvycom.auth_service.admin.service;

import com.savvycom.auth_service.admin.dto.request.CreatePermissionRequest;
import com.savvycom.auth_service.admin.dto.request.UpdatePermissionRequest;
import com.savvycom.auth_service.admin.dto.response.PermissionResponse;

import java.util.List;

public interface AdminPermissionService {
    List<PermissionResponse> listPermissions();
    PermissionResponse createPermission(CreatePermissionRequest request);
    PermissionResponse updatePermission(Long permissionId, UpdatePermissionRequest request);
    void deletePermission(Long permissionId);
}
