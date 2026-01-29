package com.savvycom.auth_service.repository;

import com.savvycom.auth_service.entity.UserSchoolScope;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface UserSchoolScopeRepository extends JpaRepository<UserSchoolScope, UserSchoolScope.PK> {
    List<UserSchoolScope> findByUserId(Long userId);

    List<UserSchoolScope> findByUserIdIn(Collection<Long> userIds);

    void deleteByUserId(Long userId);
}
