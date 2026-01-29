package com.savvycom.auth_service.service.impl;

import com.savvy.common.exception.BusinessException;
import com.savvy.common.exception.ErrorCode;
import com.savvycom.auth_service.dto.request.*;
import com.savvycom.auth_service.dto.response.*;
import com.savvycom.auth_service.entity.*;
import com.savvycom.auth_service.helper.AuthTokenHelper;
import com.savvycom.auth_service.helper.AuthUserMapper;
import com.savvycom.auth_service.repository.*;
import com.savvycom.auth_service.service.AuthService;
import com.savvycom.auth_service.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final String DEFAULT_ROLE_STUDENT = "STUDENT";
    private static final String STATUS_ACTIVE = "ACTIVE";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserSchoolScopeRepository userSchoolScopeRepository;
    private final UserStudentRepository userStudentRepository;

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;
    private final AuthTokenHelper tokenHelper;
    private final AuthUserMapper userMapper;

    @Value("${application.security.jwt.expiration}")
    private long accessTokenExpirationMs;

    @Override
    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        String email = userMapper.normalizeEmail(request.getEmail());

        if (userRepository.existsByEmail(email)) {
            throw new BusinessException(ErrorCode.DUPLICATE_RESOURCE, "Email already exists");
        }

        Role studentRole = roleRepository.findByName(DEFAULT_ROLE_STUDENT)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.INTERNAL_SERVER_ERROR,
                        "Missing role STUDENT (seed not run?)"
                ));

        Instant now = Instant.now();

        // username: nếu bạn chưa có input username trong request -> có thể derive từ email
        String username = deriveUsername(email);

        User user = User.builder()
                .email(email)
                .username(username)
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .enabled(true)
                .createdAt(now)
                .updatedAt(now)
                .build();

        user.getRoles().add(studentRole);
        userRepository.save(user);

        // save data scope schoolIds
        List<Long> schoolIds = request.getSchoolIds();
        if (schoolIds != null) {
            for (Long sid : schoolIds) {
                userSchoolScopeRepository.save(UserSchoolScope.builder()
                        .userId(user.getId())
                        .schoolId(sid)
                        .build());
            }
        }

        // save student mapping
        Long studentId = request.getStudentId();
        if (studentId != null) {
            userStudentRepository.save(UserStudent.builder()
                    .userId(user.getId())
                    .studentId(studentId)
                    .createdAt(now)
                    .build());
        }

        return userMapper.toRegisterResponse(user, schoolIds, studentId);
    }

    @Override
    @Transactional
    public TokenResponse login(LoginRequest request) {
        String email = userMapper.normalizeEmail(request.getEmail());

        authenticate(email, request.getPassword());

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_CREDENTIALS, "Invalid credentials"));

        assertUserEnabled(user);

        // load scopes
        List<Long> schoolIds = userMapper.loadSchoolIds(user.getId());
        Long studentId = userStudentRepository.findById(user.getId())
                .map(UserStudent::getStudentId)
                .orElse(null);

        // claims
        List<String> roles = userMapper.extractRoleNames(user);
        List<String> permissions = userMapper.extractPermissionCodes(user);

        String accessToken = jwtService.generateAccessToken(user, roles, permissions, schoolIds, studentId);
        String refreshToken = tokenHelper.issueRefreshToken(user.getId());

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(accessTokenExpirationMs / 1000)
                .build();
    }

    @Override
    @Transactional
    public TokenResponse refresh(RefreshRequest request) {
        RefreshToken old = tokenHelper.getUsableRefreshTokenOrThrow(request.getRefreshToken());

        User user = userRepository.findById(old.getUserId())
                .orElseThrow(() -> new BusinessException(ErrorCode.UNAUTHORIZED, "User not found"));

        assertUserEnabled(user);

        List<Long> schoolIds = userMapper.loadSchoolIds(user.getId());
        Long studentId = userStudentRepository.findById(user.getId())
                .map(UserStudent::getStudentId)
                .orElse(null);

        List<String> roles = userMapper.extractRoleNames(user);
        List<String> permissions = userMapper.extractPermissionCodes(user);

        String newAccessToken = jwtService.generateAccessToken(user, roles, permissions, schoolIds, studentId);
        String newRefreshToken = tokenHelper.rotate(old);

        return TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .expiresIn(accessTokenExpirationMs / 1000)
                .build();
    }

    @Override
    @Transactional
    public void logout(LogoutRequest request) {
        // logout public: chỉ cần refresh token trong body
        RefreshToken rt = tokenHelper.getRefreshTokenOrThrow(request.getRefreshToken());
        tokenHelper.revokeIfNeeded(rt);
    }

    private void authenticate(String email, String password) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
        } catch (AuthenticationException e) {
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS, "Invalid credentials");
        }
    }

    private void assertUserEnabled(User user) {
        if (user == null || !user.isEnabled()) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "Account is disabled");
        }
    }

    private String deriveUsername(String email) {
        // đơn giản: lấy phần trước @, bạn có thể làm logic khác
        String base = email == null ? "user" : email.split("@")[0];
        base = base.replaceAll("[^a-zA-Z0-9._-]", "");
        if (base.length() < 3) base = base + "_user";
        // tránh trùng username
        String candidate = base;
        int i = 1;
        while (userRepository.existsByUsername(candidate)) {
            candidate = base + i;
            i++;
        }
        return candidate;
    }
}
