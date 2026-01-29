package com.savvycom.auth_service.repository;

import com.savvycom.auth_service.entity.UserStudent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserStudentRepository extends JpaRepository<UserStudent, Long> {
    Optional<UserStudent> findByUserId(Long userId);

    boolean existsByStudentId(Long studentId);

    List<UserStudent> findByUserIdIn(Collection<Long> userId);

    void deleteByUserId(Long userId);
}