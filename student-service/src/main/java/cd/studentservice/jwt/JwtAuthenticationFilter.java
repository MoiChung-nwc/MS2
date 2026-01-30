package cd.studentservice.jwt;

import cd.studentservice.configuration.UserContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final String H_USER_ID = "X-User-Id";
    private static final String H_STUDENT_ID = "X-Student-Id";
    private static final String H_ROLES = "X-Roles";
    private static final String H_SCHOOL_IDS = "X-School-Ids";
    private static final String H_PERMISSIONS = "X-Permissions";
    private static final String H_DATA_SCOPE = "X-Data-Scope";


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String userIdStr = request.getHeader(H_USER_ID);
        final String studentIdStr = request.getHeader(H_STUDENT_ID);
        final String rolesStr = request.getHeader(H_ROLES);
        final String schoolIdsStr = request.getHeader(H_SCHOOL_IDS);
        final String permissionsStr = request.getHeader(H_PERMISSIONS);
        final String dataScopeStr = request.getHeader(H_DATA_SCOPE);
        if (!StringUtils.hasText(userIdStr)) {
            logger.warn("No X-User-Id header found, skipping authentication");
            filterChain.doFilter(request, response);
            return;
        }

        try {
            Long userId = Long.parseLong(userIdStr);

            List<String> roles = parseCommaSeparated(rolesStr);
            List<Long> schoolIds = parseSchoolIds(schoolIdsStr);
            List<String> permissions = parseCommaSeparated(permissionsStr);
            List<Long> dataScopeSchoolIds = parseDataScopeSchoolIds(dataScopeStr);

            Long studentId = parseNullableLong(studentIdStr);

            if (roles.contains("STUDENT") && studentId == null) {
                logger.warn("Role STUDENT but missing X-Student-Id header. Skip authentication for safety.");
                filterChain.doFilter(request, response);
                return;
            }

            // Set user context
            UserContext userContext = new UserContext();
            userContext.setUserId(userId);
            userContext.setUsername(userIdStr); // still keep principal identity = sub

            userContext.setStudentId(studentId);

            userContext.setRoles(Collections.singletonList("ROLE_" + roles));
            userContext.setSchoolIds(schoolIds);
            userContext.setPermissions(permissions);
            userContext.setDataScopeSchoolIds(dataScopeSchoolIds);
            UserContext.set(userContext);

            List<SimpleGrantedAuthority> authorities = roles.stream()
                    .map(SimpleGrantedAuthority::new)
                    .toList();

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userIdStr, // principal = userIdStr (sub)
                    null,
                    authorities
            );
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);

        } catch (Exception e) {
            logger.error("Failed to process gateway headers", e);
        }

        filterChain.doFilter(request, response);
    }
    private List<String> parseCommaSeparated(String value) {
        if (!StringUtils.hasText(value)) return List.of();
        return Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    private List<Long> parseSchoolIds(String value) {
        if (!StringUtils.hasText(value)) return List.of();
        return Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Long::parseLong)
                .collect(Collectors.toList());
    }

    private Long parseNullableLong(String value) {
        if (!StringUtils.hasText(value)) return null;
        try {
            return Long.parseLong(value.trim());
        } catch (Exception e) {
            return null;
        }
    }

    private List<Long> parseDataScopeSchoolIds(String dataScopeStr) {
        if (!StringUtils.hasText(dataScopeStr)) {
            return List.of();
        }
        try {
            // Parse JSON like {"schoolIds":[1,2,3]} and extract schoolIds array
            String schoolIdsJson = dataScopeStr.replaceAll(".*\"schoolIds\":\\[([^\\]]+)\\].*", "$1");
            if (schoolIdsJson.equals(dataScopeStr)) {
                return List.of(); // No match found
            }
            return Arrays.stream(schoolIdsJson.split(","))
                    .map(String::trim)
                    .map(Long::parseLong)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.warn("Failed to parse dataScope: " + dataScopeStr, e);
            return List.of();
        }
    }

    @Override
    public void destroy() {
        UserContext.clear();
    }
}
