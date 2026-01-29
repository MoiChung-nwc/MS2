package com.savvycom.auth_service.admin.helper;

import com.savvy.common.exception.BusinessException;
import com.savvy.common.exception.ErrorCode;
import com.savvycom.auth_service.entity.Role;
import com.savvycom.auth_service.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class AdminUserRoleResolver {

    private static final String DEFAULT_ROLE_STUDENT = "STUDENT";

    private final RoleRepository roleRepository;

    public Set<Role> resolveRolesOrDefault(List<String> roleNames) {
        List<String> names = (roleNames == null || roleNames.isEmpty())
                ? List.of(DEFAULT_ROLE_STUDENT) : roleNames;

        Set<Role> roles = new HashSet<>();
        for (String rn : names) {
            if(!StringUtils.hasText(rn)) continue;
            String name = rn.trim().toUpperCase(Locale.ROOT);

            Role r = roleRepository.findByName(name)
                    .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Role not found: " + name));
            roles.add(r);
        }
        return roles;
    }
}
