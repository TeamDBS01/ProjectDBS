package com.project.config; ////package com.project.config;
////
////import com.project.services.UserService;
////import io.jsonwebtoken.Claims;
////import org.springframework.cloud.gateway.filter.GatewayFilterChain;
////import org.springframework.cloud.gateway.filter.GlobalFilter;
////import org.springframework.http.HttpStatus;
////import org.springframework.http.server.reactive.ServerHttpRequest;
////import org.springframework.http.server.reactive.ServerHttpResponse;
////import org.springframework.stereotype.Component;
////import org.springframework.util.StringUtils;
////import org.springframework.web.server.ServerWebExchange;
////import reactor.core.publisher.Mono;
////
////@Component
////public class JwtValidationFilter implements GlobalFilter {
////
////    private final UserService userService;
////
////    public JwtValidationFilter(UserService userService) {
////        this.userService = userService;
////    }
////
////    @Override
////    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
////        ServerHttpRequest request = exchange.getRequest();
////
////        if (shouldNotFilter(request.getPath().value())) {
////            return chain.filter(exchange);
////        }
////
////        String authorizationHeader = request.getHeaders().getFirst("Authorization");
////
////        if (StringUtils.isEmpty(authorizationHeader) || !authorizationHeader.startsWith("Bearer ")) {
////            return onError(exchange, "Missing or invalid Authorization header", HttpStatus.UNAUTHORIZED);
////        }
////
////        String token = authorizationHeader.substring(7);
////
////        try {
////            userService.verifyJwtAndGetClaims(authorizationHeader); // Your existing JWT verification
////            // Optionally, you can extract user info from claims and add to request headers
////            Claims claims = userService.verifyJwtAndGetClaims(authorizationHeader);
////            exchange.getAttributes().put("userId", claims.get("userId"));
////            exchange.getAttributes().put("email", claims.get("email"));
////            exchange.getAttributes().put("role", claims.get("role"));
////            return chain.filter(exchange);
////        } catch (Exception e) {
////            return onError(exchange, "Invalid JWT token: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
////        }
////    }
////
////    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
////        ServerHttpResponse response = exchange.getResponse();
////        response.setStatusCode(httpStatus);
////        return response.setComplete();
////    }
////
////    private boolean shouldNotFilter(String path) {
////        // Exclude authentication endpoints
////        return path.startsWith("/dbs/user/auth/");
////    }
////}
//
//package com.project.config;
//
//import com.project.services.UserService;
//import io.jsonwebtoken.Claims;
//import org.springframework.cloud.gateway.filter.GatewayFilter;
//import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.server.reactive.ServerHttpRequest;
//import org.springframework.http.server.reactive.ServerHttpResponse;
//import org.springframework.stereotype.Component;
//import org.springframework.util.StringUtils;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Mono;
//
//@Component
//public class JwtValidationFilter extends AbstractGatewayFilterFactory<JwtValidationFilter.Config> {
//
//    private final UserService userService;
//
//    public JwtValidationFilter(UserService userService) {
//        super(Config.class);
//        this.userService = userService;
//    }
//
//    @Override
//    public GatewayFilter apply(Config config) {
//        return (exchange, chain) -> {
//            ServerHttpRequest request = exchange.getRequest();
//
//            if (shouldNotFilter(request.getPath().value())) {
//                return chain.filter(exchange);
//            }
//
//            String authorizationHeader = request.getHeaders().getFirst("Authorization");
//
//            if (StringUtils.isEmpty(authorizationHeader) || !authorizationHeader.startsWith("Bearer ")) {
//                return onError(exchange, "Missing or invalid Authorization header", HttpStatus.UNAUTHORIZED);
//            }
//
//            String token = authorizationHeader.substring(7);
//
//            try {
//                Claims claims = userService.verifyJwtAndGetClaims(authorizationHeader);
//                exchange.getAttributes().put("userId", claims.get("userId"));
//                exchange.getAttributes().put("email", claims.get("email"));
//                exchange.getAttributes().put("role", claims.get("role"));
//                return chain.filter(exchange);
//            } catch (Exception e) {
//                return onError(exchange, "Invalid JWT token: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
//            }
//        };
//    }
//
//    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
//        ServerHttpResponse response = exchange.getResponse();
//        response.setStatusCode(httpStatus);
//        return response.setComplete();
//    }
//
//    private boolean shouldNotFilter(String path) {
//        return path.startsWith("/dbs/user/auth/");
//    }
//
//    public static class Config {
//
//    }
//}