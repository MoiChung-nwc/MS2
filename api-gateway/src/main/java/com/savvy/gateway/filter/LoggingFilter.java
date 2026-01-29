package com.savvy.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Component
public class LoggingFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String requestId = UUID.randomUUID().toString();
        ServerHttpRequest request = exchange.getRequest();
        
        log.info("Request ID: {} | Method: {} | Path: {} | Headers: {}",
                requestId,
                request.getMethod(),
                request.getPath(),
                request.getHeaders());
        
        // Add request ID to headers
        ServerHttpRequest modifiedRequest = request.mutate()
                .header("X-Request-ID", requestId)
                .build();
        
        ServerWebExchange modifiedExchange = exchange.mutate()
                .request(modifiedRequest)
                .build();
        
        long startTime = System.currentTimeMillis();
        
        return chain.filter(modifiedExchange)
                .doFinally(signalType -> {
                    long duration = System.currentTimeMillis() - startTime;
                    log.info("Request ID: {} | Status: {} | Duration: {}ms",
                            requestId,
                            exchange.getResponse().getRawStatusCode(),
                            duration);
                });
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
