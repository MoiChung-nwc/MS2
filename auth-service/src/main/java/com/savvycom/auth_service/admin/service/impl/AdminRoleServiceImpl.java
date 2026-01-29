package com.savvycom.auth_service.admin.service.impl;

import com.savvy.common.exception.BusinessException;
import com.savvy.common.exception.ErrorCode;
import com.savvycom.auth_service.admin.dto.request.CreateRoleRequest;
import com.savvycom.auth_service.admin.dto.request.UpdateRoleRequest;
import com.savvycom.auth_service.admin.dto.response.RoleResponse;
import com.savvycom.auth_service.admin.service.AdminRoleService;
import com.savvycom.auth_service.entity.Permission;
import com.savvycom.auth_service.entity.Role;
import com.savvycom.auth_service.repository.PermissionRepository;
import com.savvycom.auth_service.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminRoleServiceImpl implements AdminRoleService {

    private static final Set<String> SYSTEM_ROLES = Set.of("ADMIN", "SCHOOL_MANAGER", "STUDENT");

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Override
    @Transactional(readOnly = true)
    public List<RoleResponse> listRoles() {
        return roleRepository.findAll(Sort.by(Sort.Direction.ASC, "name"))
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public RoleResponse createRole(CreateRoleRequest request) {
        String name = normalizeRoleName(request.getName());

        if (roleRepository.existsByName(name)) {
            throw new BusinessException(ErrorCode.DUPLICATE_RESOURCE, "Role already exists: " + name);
        }

        Set<Permission> perms = resolvePermissions(request.getPermissionCodes());

        Role role = Role.builder()
                .name(name)
                .description(trimToNull(request.getDescription()))
                .permissions(perms)
                .build();

        return toResponse(roleRepository.save(role));
    }

    @Override
    @Transactional
    public RoleResponse updateRole(Long roleId, UpdateRoleRequest request) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Role not found"));

        if (request.getDescription() != null) {
            role.setDescription(trimToNull(request.getDescription()));
        }

        if (request.getPermissionCodes() != null) {
            role.setPermissions(resolvePermissions(request.getPermissionCodes()));
        }

        return toResponse(roleRepository.save(role));
    }

    @Override
    @Transactional
    public void deleteRole(Long roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Role not found"));

        if (SYSTEM_ROLES.contains(role.getName())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "Cannot delete system role: " + role.getName());
        }

        roleRepository.delete(role);
    }

    private RoleResponse toResponse(Role r) {
        List<String> permissionCodes = (r.getPermissions() == null ? List.<String>of() :
                r.getPermissions().stream()
                        .map(Permission::getCode)
                        .filter(Objects::nonNull)
                        .distinct()
                        .sorted()
                        .toList());

        return RoleResponse.builder()
                .id(r.getId())
                .name(r.getName())
                .description(r.getDescription())
                .permissions(permissionCodes)
                .build();
    }

    private Set<Permission> resolvePermissions(List<String> codes) {
        if (codes == null || codes.isEmpty()) return new HashSet<>();

        List<String> normalized = codes.stream()
                .filter(StringUtils::hasText)
                .map(s -> s.trim().toUpperCase(Locale.ROOT))
                .distinct()
                .toList();

        Map<String, Permission> found = permissionRepository.findByCodeIn(normalized)
                .stream()
                .collect(Collectors.toMap(Permission::getCode, p -> p));

        List<String> missing = normalized.stream()
                .filter(c -> !found.containsKey(c))
                .toList();

        if (!missing.isEmpty()) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Permissions not found: " + String.join(",", missing));
        }

        return new HashSet<>(found.values());
    }

    private String normalizeRoleName(String name) {
        if (!StringUtils.hasText(name)) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "Role name is required");
        }
        return name.trim().toUpperCase(Locale.ROOT);
    }

    private String trimToNull(String s) {
        if (!StringUtils.hasText(s)) return null;
        return s.trim();
    }
}
