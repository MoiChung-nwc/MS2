package com.savvycom.auth_service.helper;

import com.savvycom.auth_service.dto.response.MeResponse;
import com.savvycom.auth_service.dto.response.RegisterResponse;
import com.savvycom.auth_service.entity.Role;
import com.savvycom.auth_service.entity.User;
import com.savvycom.auth_service.entity.UserSchoolScope;
import com.savvycom.auth_service.repository.UserSchoolScopeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;

@Component
@RequiredArgsConstructor
public class AuthUserMapper {

    private final UserSchoolScopeRepository userSchoolScopeRepository;

    public String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase(Locale.ROOT);
    }

    public List<String> extractRoleNames(User user) {
        if (user == null || user.getRoles() == null) return List.of();

        return user.getRoles().stream()
                .map(Role::getName)
                .distinct()
                .toList();
    }

    public List<String> extractPermissionCodes(User user) {
        if (user == null || user.getRoles() == null) return List.of();

        return user.getRoles().stream()
                .flatMap(r -> r.getPermissions().stream())
                .map(p -> p.getCode())
                .distinct()
                .toList();
    }

    public RegisterResponse toRegisterResponse(User user, List<Long> schoolIds, Long studentId) {
        return RegisterResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(extractRoleNames(user))
                .studentId(studentId)
                .dataScope(RegisterResponse.DataScope.builder().schoolIds(schoolIds).build())
                .build();
    }

    public MeResponse toMeResponse(User user, List<Long> schoolIds) {
        return MeResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(extractRoleNames(user))
                .permissions(extractPermissionCodes(user))
                .dataScope(MeResponse.DataScope.builder().schoolIds(schoolIds).build())
                .build();
    }

    public List<Long> loadSchoolIds(Long userId) {
        return userSchoolScopeRepository.findByUserId(userId).stream()
                .map(UserSchoolScope::getSchoolId)
                .distinct()
                .toList();
    }
}
