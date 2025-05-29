package com.microservices.gateway.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import reactor.core.publisher.Mono;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Configuration
@EnableWebFluxSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    /**
     * CORS toàn cục cho gateway
     */
    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("http://localhost:3000");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsWebFilter(source);
    }

    /**
     * Chain 0: public endpoints, permitAll, không bật JWT filter
     */
    @Bean
    @Order(0)
    public SecurityWebFilterChain publicChain(ServerHttpSecurity http) {
        http
                .csrf(csrf -> csrf.disable())
                .securityMatcher(ServerWebExchangeMatchers.pathMatchers(
                        "/eureka/**",
                        "/api/v1/users/**",
                        "/api/v1/notifications/**",
                        "/api/v1/posts/**"
                ))
                .authorizeExchange(ex -> ex.anyExchange().permitAll())
        // Tắt resource-server cho public chain
        ; // no OAuth2 resource server for public endpoints
        return http.build();
    }

    /**
     * Chain 1: protected endpoints, yêu cầu JWT
     */
    @Bean
    @Order(1)
    public SecurityWebFilterChain protectedChain(ServerHttpSecurity http) {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeExchange(ex -> ex.anyExchange().authenticated())
                .oauth2ResourceServer(spec -> spec
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(grantedAuthoritiesExtractor())
                        )
                );
        return http.build();
    }

    /**
     * Converter trích các role từ claim "realm_access.roles"
     */
    private Converter<Jwt, Mono<AbstractAuthenticationToken>> grantedAuthoritiesExtractor() {
        return jwt -> {
            @SuppressWarnings("unchecked")
            List<String> roles = (List<String>) ((Map<String, Object>) jwt.getClaim("realm_access"))
                    .getOrDefault("roles", List.of());

            Collection<GrantedAuthority> authorities = roles.stream()
                    .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
                    .collect(Collectors.toList());

            logger.debug("⚡ JWT Token: {}", jwt.getTokenValue());
            logger.debug("⚡ User Roles: {}", authorities);

            return Mono.just(new JwtAuthenticationToken(jwt, authorities));
        };
    }
}
