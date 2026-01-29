package com.savvy.gateway.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.savvy.common.dto.BaseResponse;
import com.savvy.common.exception.ErrorCode;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.regex.Pattern;

@Slf4j
@Component
@RequiredArgsConstructor
public class GatewayCentralAuthFilter implements GlobalFilter, Ordered {

    private static final String REQUEST_ID_HEADER = "X-Request-ID";

    private static final String H_USER_ID = "X-User-Id";
    private static final String H_ROLES = "X-Roles";
    private static final String H_SCHOOL_IDS = "X-School-Ids";
    private static final String H_INTERNAL = "X-Internal-Auth";

    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    @Value("${gateway.internal.secret:}")
    private String internalSecret;

    private final ObjectMapper om;

    // Public endpoints (logout public theo yêu cầu)
    private static final Set<String> PUBLIC_PATHS = Set.of(
            "/api/v1/auth/ping",
            "/api/v1/auth/login",
            "/api/v1/auth/register",
            "/api/v1/auth/refresh",
            "/api/v1/auth/logout"
    );

    private static final List<String> PUBLIC_PREFIX = List.of(
            "/api/v1/gateway",
            "/actuator"
    );

    private static final Pattern HEX = Pattern.compile("^[0-9a-fA-F]+$");

    private volatile SecretKey cachedKey; // cache key (lazy)

    @Override
    public int getOrder() {
        // chạy sau LoggingFilter (nếu LoggingFilter là HIGHEST_PRECEDENCE)
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        final String path = exchange.getRequest().getURI().getPath();

        if (isPublic(path)) {
            return chain.filter(exchange);
        }

        final String auth = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (!StringUtils.hasText(auth) || !auth.startsWith("Bearer ")) {
            return writeError(exchange, ErrorCode.UNAUTHORIZED, "Missing token");
        }

        final Claims claims;
        try {
            claims = parseToken(auth.substring(7).trim());
        } catch (ExpiredJwtException e) {
            return writeError(exchange, ErrorCode.TOKEN_EXPIRED, "Token expired");
        } catch (JwtException e) {
            return writeError(exchange, ErrorCode.TOKEN_INVALID, "Invalid token");
        }

        final String userId = claims.getSubject(); // sub
        final Set<String> roles = new LinkedHashSet<>(asStringList(claims.get("roles")));
        final Set<Long> schoolIds = extractSchoolIds(claims.get("dataScope"));

        // ===== RBAC at Gateway (MVP) =====
        if (path.startsWith("/api/v1/students/")) {
            // cho STUDENT đọc (theo docs scope-filtered)
            if (!hasAnyRole(roles, "ADMIN", "SCHOOL_MANAGER", "STUDENT")) {
                return writeError(exchange, ErrorCode.FORBIDDEN, "Insufficient role for student-service");
            }
        }

        if (path.startsWith("/api/v1/grades")) {
            HttpMethod method = exchange.getRequest().getMethod();
            boolean isWrite = method == HttpMethod.POST || method == HttpMethod.PUT || method == HttpMethod.DELETE;

            if (isWrite) {
                if (!hasAnyRole(roles, "ADMIN", "SCHOOL_MANAGER")) {
                    return writeError(exchange, ErrorCode.FORBIDDEN, "Only ADMIN/SCHOOL_MANAGER can write grades");
                }
            } else {
                if (!hasAnyRole(roles, "ADMIN", "SCHOOL_MANAGER", "STUDENT")) {
                    return writeError(exchange, ErrorCode.FORBIDDEN, "Insufficient role for grade-service");
                }
            }
        }

        // ===== Inject internal headers (Option 2) =====
        String requestId = exchange.getRequest().getHeaders().getFirst(REQUEST_ID_HEADER);

        ServerHttpRequest mutated = exchange.getRequest().mutate()
                .headers(h -> {
                    // Security: strip sensitive headers from client
                    h.remove(HttpHeaders.AUTHORIZATION);
                    h.remove(H_USER_ID);
                    h.remove(H_ROLES);
                    h.remove(H_SCHOOL_IDS);
                    h.remove(H_INTERNAL);

                    if (StringUtils.hasText(userId)) h.set(H_USER_ID, userId);
                    h.set(H_ROLES, String.join(",", roles));
                    h.set(H_SCHOOL_IDS, joinLongs(schoolIds));

                    if (StringUtils.hasText(internalSecret)) {
                        h.set(H_INTERNAL, internalSecret);
                    }

                    if (StringUtils.hasText(requestId)) {
                        h.set(REQUEST_ID_HEADER, requestId);
                    }
                })
                .build();

        return chain.filter(exchange.mutate().request(mutated).build());
    }

    private boolean isPublic(String path) {
        if (PUBLIC_PATHS.contains(path)) return true;
        for (String p : PUBLIC_PREFIX) if (path.startsWith(p)) return true;
        return false;
    }

    private Claims parseToken(String token) {
        SecretKey key = getOrBuildKey();
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private SecretKey getOrBuildKey() {
        SecretKey k = cachedKey;
        if (k != null) return k;
        synchronized (this) {
            if (cachedKey == null) {
                cachedKey = Keys.hmacShaKeyFor(decodeSecret(secretKey));
            }
            return cachedKey;
        }
    }

    private byte[] decodeSecret(String s) {
        String t = s.trim();

        // If looks like hex, decode hex
        if (t.length() % 2 == 0 && HEX.matcher(t).matches()) {
            byte[] out = new byte[t.length() / 2];
            for (int i = 0; i < out.length; i++) {
                int hi = Character.digit(t.charAt(i * 2), 16);
                int lo = Character.digit(t.charAt(i * 2 + 1), 16);
                out[i] = (byte) ((hi << 4) + lo);
            }
            return out;
        }

        // else treat as Base64
        return Base64.getDecoder().decode(t);
    }

    private List<String> asStringList(Object v) {
        if (v == null) return List.of();
        if (v instanceof Collection<?> c) {
            List<String> out = new ArrayList<>();
            for (Object o : c) out.add(String.valueOf(o));
            return out;
        }
        return List.of(String.valueOf(v));
    }

    @SuppressWarnings("unchecked")
    private Set<Long> extractSchoolIds(Object dataScopeObj) {
        Set<Long> ids = new LinkedHashSet<>();
        if (dataScopeObj instanceof Map<?, ?> dataScope) {
            Object schoolIdsObj = dataScope.get("schoolIds");
            if (schoolIdsObj instanceof Collection<?> c) {
                for (Object o : c) {
                    Long v = tryParseLong(String.valueOf(o));
                    if (v != null) ids.add(v);
                }
            }
        }
        return ids;
    }

    private boolean hasAnyRole(Set<String> roles, String... required) {
        for (String r : required) if (roles.contains(r)) return true;
        return false;
    }

    private String joinLongs(Set<Long> nums) {
        if (nums == null || nums.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (Long n : nums) {
            if (sb.length() > 0) sb.append(",");
            sb.append(n);
        }
        return sb.toString();
    }

    private Long tryParseLong(String s) {
        try {
            return Long.parseLong(s.trim());
        } catch (Exception e) {
            return null;
        }
    }

    private Mono<Void> writeError(ServerWebExchange exchange, ErrorCode code, String details) {
        var response = exchange.getResponse();
        response.setStatusCode(code.getHttpStatus());
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        // propagate request id
        String requestId = exchange.getRequest().getHeaders().getFirst(REQUEST_ID_HEADER);
        if (StringUtils.hasText(requestId)) {
            response.getHeaders().set(REQUEST_ID_HEADER, requestId);
        }

        BaseResponse<?> body = BaseResponse.error(
                code.getCode(),
                code.getMessage(),
                code.getHttpStatus(),
                details
        );

        try {
            byte[] bytes = om.writeValueAsBytes(body);
            return response.writeWith(Mono.just(response.bufferFactory().wrap(bytes)));
        } catch (Exception e) {
            return Mono.error(e);
        }
    }
}
