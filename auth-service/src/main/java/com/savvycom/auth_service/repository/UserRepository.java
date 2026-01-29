package com.savvycom.auth_service.repository;

import com.savvycom.auth_service.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);

    @EntityGraph(attributePaths = {"roles"})
    Page<User> findAll(Pageable pageable);
    List<User> findAllByRoles_Id(Long id);
}
