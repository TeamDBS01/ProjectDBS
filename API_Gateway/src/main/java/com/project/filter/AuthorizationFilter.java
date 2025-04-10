package com.project.filter;

import com.project.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Component
public class AuthorizationFilter extends AbstractGatewayFilterFactory<AuthorizationFilter.Config> {

    @Autowired
    private JwtUtil jwtUtil;

    public AuthorizationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            String authHeader = request.getHeaders().get("Authorization").get(0);
            String token = authHeader.substring(7);

            String userRole = jwtUtil.getRoleFromToken(token);

            if (config.getRoles().contains(userRole)) {
                return chain.filter(exchange);
            }

            return onError(exchange, "Unauthorized - Insufficient role", HttpStatus.FORBIDDEN);
        };
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        return response.setComplete();
    }

    public static class Config {
        private List<String> roles;

        public List<String> getRoles() {
            return roles;
        }

        public void setRoles(List<String> roles) {
            this.roles = roles;
        }
    }

    public static class ConfigBuilder {
        private List<String> roles;

        public ConfigBuilder roles(String... roles) {
            this.roles = Arrays.asList(roles);
            return this;
        }

        public Config build() {
            Config config = new Config();
            config.setRoles(this.roles);
            return config;
        }
    }
}