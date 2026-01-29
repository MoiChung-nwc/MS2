package com.savvycom.auth_service.seed;

import com.savvycom.auth_service.entity.Permission;
import com.savvycom.auth_service.entity.Role;
import com.savvycom.auth_service.repository.PermissionRepository;
import com.savvycom.auth_service.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Order(1)
public class RbacSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Override
    @Transactional
    public void run(String... args) {
        Permission systemAdmin = upsertPerm("SYSTEM_ADMIN", "Bypass all authorization checks");

        Permission schoolRead  = upsertPerm("SCHOOL_READ", "View schools");
        Permission schoolWrite = upsertPerm("SCHOOL_WRITE", "Create/update schools");

        Permission classRead   = upsertPerm("CLASS_READ", "View classes");
        Permission classWrite  = upsertPerm("CLASS_WRITE", "Create/update classes");

        Permission studentRead  = upsertPerm("STUDENT_READ", "View students");
        Permission studentWrite = upsertPerm("STUDENT_WRITE", "Create/update students");

        Permission gradeRead   = upsertPerm("GRADE_READ", "View grades");
        Permission gradeWrite  = upsertPerm("GRADE_WRITE", "Create/update grades");

        upsertRole("ADMIN", "System administrator",
                Set.of(systemAdmin)
        );

        upsertRole("SCHOOL_MANAGER", "School manager (scoped by schoolIds)",
                Set.of(schoolRead, schoolWrite, classRead, classWrite, studentRead, studentWrite, gradeRead, gradeWrite)
        );

        upsertRole("STUDENT", "Student (only view own data)",
                Set.of(classRead, gradeRead, studentRead)
        );
    }

    private Permission upsertPerm(String code, String desc) {
        return permissionRepository.findByCode(code)
                .map(p -> {
                    p.setDescription(desc);
                    return permissionRepository.save(p);
                })
                .orElseGet(() -> permissionRepository.save(
                        Permission.builder().code(code).description(desc).build()
                ));
    }

    private void upsertRole(String name, String desc, Set<Permission> perms) {
        Role role = roleRepository.findByName(name)
                .orElseGet(() -> Role.builder().name(name).build());

        role.setDescription(desc);
        role.setPermissions(new HashSet<>(perms));
        roleRepository.save(role);
    }
}
