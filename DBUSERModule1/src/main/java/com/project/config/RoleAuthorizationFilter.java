package com.project.config;//package com.project.config;
//
//import org.springframework.cloud.gateway.filter.GatewayFilter;
//import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.server.reactive.ServerHttpResponse;
//import org.springframework.stereotype.Component;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Mono;
//
//import java.util.Map;
//
//@Component
//public class RoleAuthorizationFilter extends AbstractGatewayFilterFactory<RoleAuthorizationFilter.Config> {
//
//    public RoleAuthorizationFilter() {
//        super(Config.class);
//    }
//
//    @Override
//    public GatewayFilter apply(Config config) {
//        return (exchange, chain) -> {
//            Map<String, Object> attributes = exchange.getAttributes();
//            String userRole = (String) attributes.get("role");
//
//            if (userRole != null && userRole.equals(config.getRequiredRole())) {
//                return chain.filter(exchange);
//            } else {
//                ServerHttpResponse response = exchange.getResponse();
//                response.setStatusCode(HttpStatus.FORBIDDEN);
//                return response.setComplete();
//            }
//        };
//    }
//
//    public static class Config {
//        private String requiredRole;
//
//        public String getRequiredRole() {
//            return requiredRole;
//        }
//
//        public void setRequiredRole(String requiredRole) {
//            this.requiredRole = requiredRole;
//        }
//    }
//}