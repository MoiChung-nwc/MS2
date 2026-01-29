package com.savvycom.auth_service.repository;

import com.savvycom.auth_service.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
    boolean existsByName(String name);
    List<Role> findByNameIn(Collection<String> names);
    List<Role> findAllByPermissions_Id(Long permissionId);
}
