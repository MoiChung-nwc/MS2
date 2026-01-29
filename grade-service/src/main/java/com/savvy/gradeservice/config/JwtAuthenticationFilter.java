package com.savvy.gradeservice.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String H_USER_ID = "X-User-Id";
    private static final String H_ROLES = "X-Roles";
    private static final String H_SCHOOL_IDS = "X-School-Ids";

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String userIdStr = request.getHeader(H_USER_ID);
        final String rolesStr = request.getHeader(H_ROLES);
        final String schoolIdsStr = request.getHeader(H_SCHOOL_IDS);

        if (!StringUtils.hasText(userIdStr)) {
            filterChain.doFilter(request, response);
            return;
        }
        
        try {
            Long userId = Long.parseLong(userIdStr);
            List<String> roles = parseCommaSeparated(rolesStr);
            List<Long> schoolIds = parseSchoolIds(schoolIdsStr);

            // Set user context
            UserContext userContext = new UserContext();
            userContext.setUserId(userId);
            userContext.setUsername(userIdStr); // Use userId as username
            userContext.setRoles(roles);
            userContext.setSchoolIds(schoolIds);
            UserContext.set(userContext);

            // Set Spring Security context
            List<SimpleGrantedAuthority> authorities = roles.stream()
                    .map(SimpleGrantedAuthority::new)
                    .toList();

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userIdStr,
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
        if (!StringUtils.hasText(value)) {
            return List.of();
        }
        return Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    private List<Long> parseSchoolIds(String value) {
        if (!StringUtils.hasText(value)) {
            return List.of();
        }
        return Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Long::parseLong)
                .collect(Collectors.toList());
    }

    @Override
    public void destroy() {
        UserContext.clear();
    }
}
