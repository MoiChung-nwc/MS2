package com.savvycom.auth_service.config;

import com.savvy.common.exception.ErrorCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class InternalAuthFilter extends OncePerRequestFilter {

    private static final String H_USER_ID = "X-User-Id";
    private static final String H_ROLES = "X-Roles";
    private static final String H_INTERNAL = "X-Internal-Auth";

    @Value("${gateway.internal.secret:}")
    private String internalSecret;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/api/v1/auth/");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String secret = request.getHeader(H_INTERNAL);
        if(!StringUtils.hasText(internalSecret) || !internalSecret.equals(secret)) {
            response.setStatus(ErrorCode.UNAUTHORIZED.getHttpStatus().value());
            response.setContentType("application/json");
            response.getWriter().write("{\"success\":false,\"status\":401,\"message\":\"Missing/invalid internal auth\"}");
            return;
        }

        String userId = request.getHeader(H_USER_ID);
        String roleHeader = request.getHeader(H_ROLES);

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        if(StringUtils.hasText(roleHeader)) {
            for (String r : roleHeader.split(",")) {
                String role = r.trim();
                if(!role.isEmpty()) {
                    authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
                }
            }
        }
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userId, null, authorities);

        SecurityContextHolder.getContext().setAuthentication(auth);

        filterChain.doFilter(request, response);
    }
}
