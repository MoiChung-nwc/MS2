package com.savvycom.auth_service.admin.service;

import com.savvy.common.dto.PageResponse;
import com.savvycom.auth_service.admin.dto.request.AdminCreateUserRequest;
import com.savvycom.auth_service.admin.dto.request.AdminSetUserRolesRequest;
import com.savvycom.auth_service.admin.dto.request.AdminUpdateUserRequest;
import com.savvycom.auth_service.admin.dto.response.AdminUserDetailResponse;
import com.savvycom.auth_service.admin.dto.response.AdminUserSummaryResponse;
import org.springframework.data.domain.Pageable;

public interface AdminUserService {
    PageResponse<AdminUserSummaryResponse> listUsers(Pageable pageable);
    AdminUserDetailResponse getUser(Long userId);
    AdminUserDetailResponse createUser(AdminCreateUserRequest request);
    AdminUserDetailResponse updateUser(Long userId, AdminUpdateUserRequest request);
    void deleteUser(Long userId);
    AdminUserDetailResponse setUserRoles(Long userId, AdminSetUserRolesRequest request);
}
