//package com.savvy.gateway.filter;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.cloud.gateway.filter.GatewayFilter;
//import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
//import org.springframework.stereotype.Component;
//
//@Slf4j
//@Component
//public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {
//
//    public AuthenticationFilter() {
//        super(Config.class);
//    }
//
//    @Override
//    public GatewayFilter apply(Config config) {
//        return (exchange, chain) -> {
//            String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
//
//            if (authHeader != null && authHeader.startsWith("Bearer ")) {
//                String token = authHeader.substring(7);
//                log.debug("Processing token: {}...", token.substring(0, Math.min(token.length(), 10)));
//            }
//
//            return chain.filter(exchange);
//        };
//    }
//
//    public static class Config {
//        // Configuration properties if needed
//    }
//}
