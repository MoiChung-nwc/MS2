package com.savvycom.auth_service.admin.service;

import com.savvycom.auth_service.admin.dto.request.CreateRoleRequest;
import com.savvycom.auth_service.admin.dto.request.UpdateRoleRequest;
import com.savvycom.auth_service.admin.dto.response.RoleResponse;

import java.util.List;

public interface AdminRoleService {
    List<RoleResponse> listRoles();
    RoleResponse createRole(CreateRoleRequest request);
    RoleResponse updateRole(Long roleId, UpdateRoleRequest request);
    void deleteRole(Long roleId);
}
