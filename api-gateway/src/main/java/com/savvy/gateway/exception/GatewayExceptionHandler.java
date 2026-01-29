package com.savvy.gateway.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.savvy.common.dto.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.ConnectException;
import java.util.concurrent.TimeoutException;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GatewayExceptionHandler implements ErrorWebExceptionHandler {

    private static final String REQUEST_ID_HEADER = "X-Request-ID";
    private final ObjectMapper objectMapper;

    public GatewayExceptionHandler() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();

        if (response.isCommitted()) {
            return Mono.error(ex);
        }

        // Propagate X-Request-ID if present (LoggingFilter usually sets it)
        String requestId = exchange.getRequest().getHeaders().getFirst(REQUEST_ID_HEADER);
        if (requestId != null && !requestId.isBlank()) {
            response.getHeaders().set(REQUEST_ID_HEADER, requestId);
        }

        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        HttpStatus status = determineHttpStatus(ex);
        response.setStatusCode(status);

        BaseResponse<?> body = createErrorResponse(ex, status);

        log.error("Gateway error | requestId={} | status={} | path={} | message={}",
                requestId,
                status.value(),
                exchange.getRequest().getPath(),
                ex.getMessage(),
                ex);

        try {
            byte[] bytes = objectMapper.writeValueAsBytes(body);
            DataBuffer buffer = response.bufferFactory().wrap(bytes);
            return response.writeWith(Mono.just(buffer));
        } catch (Exception e) {
            log.error("Error writing gateway error response", e);
            return Mono.error(e);
        }
    }

    private HttpStatus determineHttpStatus(Throwable ex) {
        if (ex instanceof ResponseStatusException rse) {
            // Many Spring errors become ResponseStatusException already
            return HttpStatus.valueOf(rse.getStatusCode().value());
        }

        // Timeouts
        if (ex instanceof TimeoutException || hasCause(ex, TimeoutException.class)) {
            return HttpStatus.GATEWAY_TIMEOUT; // 504
        }

        // Downstream connect issue
        if (ex instanceof ConnectException || hasCause(ex, ConnectException.class)) {
            return HttpStatus.SERVICE_UNAVAILABLE; // 503
        }

        // Gateway NotFoundException: could be no route OR no instance
        if (ex instanceof NotFoundException) {
            String msg = ex.getMessage() == null ? "" : ex.getMessage().toLowerCase();

            // Heuristic for "no instance available"
            if (msg.contains("unable to find instance") || msg.contains("no instance")) {
                return HttpStatus.SERVICE_UNAVAILABLE; // 503
            }
            return HttpStatus.NOT_FOUND; // 404 route not found
        }

        return HttpStatus.INTERNAL_SERVER_ERROR; // 500
    }

    private BaseResponse<?> createErrorResponse(Throwable ex, HttpStatus status) {
        int http = status.value();

        // Gateway-level error codes (separate from business services)
        String code;
        String message;

        switch (http) {
            case 404 -> {
                code = "9001";
                message = "Route not found";
            }
            case 503 -> {
                code = "9002";
                message = "Service not available";
            }
            case 504 -> {
                code = "9003";
                message = "Gateway timeout";
            }
            default -> {
                code = "9000";
                message = "Gateway internal error";
            }
        }

        // details: keep it short, useful for debug; avoid stacktrace exposure
        String details = ex.getMessage();

        return BaseResponse.error(code, message, status, details);
    }

    private boolean hasCause(Throwable ex, Class<? extends Throwable> type) {
        Throwable cur = ex;
        while (cur != null) {
            if (type.isInstance(cur)) return true;
            cur = cur.getCause();
        }
        return false;
    }
}
