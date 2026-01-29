package com.savvycom.auth_service.admin.controller;

import com.savvy.common.dto.BaseResponse;
import com.savvycom.auth_service.admin.dto.response.RoleResponse;
import com.savvycom.auth_service.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/roles")
public class AdminRoleController {

    private final RoleRepository roleRepository;


}
