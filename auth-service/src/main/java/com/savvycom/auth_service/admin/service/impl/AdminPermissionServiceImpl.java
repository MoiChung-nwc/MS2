package com.savvycom.auth_service.admin.service.impl;

import com.savvy.common.exception.BusinessException;
import com.savvy.common.exception.ErrorCode;
import com.savvycom.auth_service.admin.dto.request.CreatePermissionRequest;
import com.savvycom.auth_service.admin.dto.request.UpdatePermissionRequest;
import com.savvycom.auth_service.admin.dto.response.PermissionResponse;
import com.savvycom.auth_service.admin.service.AdminPermissionService;
import com.savvycom.auth_service.entity.Permission;
import com.savvycom.auth_service.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class AdminPermissionServiceImpl implements AdminPermissionService {

    private final PermissionRepository permissionRepository;

    @Override
    @Transactional(readOnly = true)
    public List<PermissionResponse> listPermissions() {
        return permissionRepository.findAll(Sort.by(Sort.Direction.ASC, "code"))
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public PermissionResponse createPermission(CreatePermissionRequest request) {
        String code = normalizeCode(request.getCode());

        if (permissionRepository.existsByCode(code)) {
            throw new BusinessException(ErrorCode.DUPLICATE_RESOURCE, "Permission code already exists: " + code);
        }

        Permission p = Permission.builder()
                .code(code)
                .description(trimToNull(request.getDescription()))
                .build();

        return toResponse(permissionRepository.save(p));
    }

    @Override
    @Transactional
    public PermissionResponse updatePermission(Long permissionId, UpdatePermissionRequest request) {
        Permission p = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Permission not found"));

        // Optional: cho phép đổi code
        if (StringUtils.hasText(request.getCode())) {
            String newCode = normalizeCode(request.getCode());
            if (!newCode.equalsIgnoreCase(p.getCode()) && permissionRepository.existsByCode(newCode)) {
                throw new BusinessException(ErrorCode.DUPLICATE_RESOURCE, "Permission code already exists: " + newCode);
            }
            p.setCode(newCode);
        }

        if (request.getDescription() != null) {
            p.setDescription(trimToNull(request.getDescription()));
        }

        return toResponse(permissionRepository.save(p));
    }

    @Override
    @Transactional
    public void deletePermission(Long permissionId) {
        if (!permissionRepository.existsById(permissionId)) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Permission not found");
        }
        permissionRepository.deleteById(permissionId);
    }

    private PermissionResponse toResponse(Permission p) {
        return PermissionResponse.builder()
                .id(p.getId())
                .code(p.getCode())
                .description(p.getDescription())
                .build();
    }

    private String normalizeCode(String code) {
        if (!StringUtils.hasText(code)) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "Permission code is required");
        }
        return code.trim().toUpperCase(Locale.ROOT);
    }

    private String trimToNull(String s) {
        if (!StringUtils.hasText(s)) return null;
        return s.trim();
    }
}
