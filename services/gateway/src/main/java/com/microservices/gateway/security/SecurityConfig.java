package com.microservices.gateway.security;

//import java.util.Collection;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//import org.slf4j.LoggerFactory;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.convert.converter.Converter;
//import org.springframework.security.authentication.AbstractAuthenticationToken;
//import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
//import org.springframework.security.config.web.server.ServerHttpSecurity;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.oauth2.jwt.Jwt;
//import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
//import org.springframework.security.web.server.SecurityWebFilterChain;
//
//import reactor.core.publisher.Mono;
//
//@Configuration
//@EnableWebFluxSecurity
//public class SecurityConfig {
//
//    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(SecurityConfig.class);
//
//    @Bean
//    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
//        http
//                .csrf(ServerHttpSecurity.CsrfSpec::disable)
//                .authorizeExchange(exchange -> exchange
//                        .pathMatchers("/eureka/**").permitAll()
//                        .pathMatchers("/api/v1/users/**").permitAll()
//                        .anyExchange().authenticated())
//                .oauth2ResourceServer(oauth2 ->
//                        oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(grantedAuthoritiesExtractor()))
//                );
//
//        return http.build();
//    }
//
//    private Converter<Jwt, Mono<AbstractAuthenticationToken>> grantedAuthoritiesExtractor() {
//        return jwt -> {
//            Map<String, Object> realmAccess = jwt.getClaim("realm_access");
//            List<String> roles = realmAccess != null
//                    ? (List<String>) realmAccess.get("roles")
//                    : List.of();
//
//            logger.debug("⚡ JWT Token: {}", jwt.getTokenValue());
//            logger.debug("⚡ User Roles: {}", roles);
//
//            Collection<GrantedAuthority> authorities = roles.stream()
//                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
//                    .collect(Collectors.toList());
//
//            return Mono.just(new JwtAuthenticationToken(jwt, authorities));
//        };
//    }
//}


import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@EnableWebFluxSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {

        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable) // Tắt CSRF nếu không cần thiết
                .authorizeExchange(exchange -> exchange
                                .pathMatchers("/eureka/**").permitAll()
                                .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(grantedAuthoritiesExtractor()))
                );
        return http.build();
    }

    private Converter<Jwt, Mono<AbstractAuthenticationToken>> grantedAuthoritiesExtractor() {

        return jwt -> {
            List<String> realmRoles = jwt.getClaim("realm_access") != null
                    ? ((List<String>) ((java.util.Map<String, Object>) jwt.getClaim("realm_access")).get("roles"))
                    : List.of();
            logger.debug("⚡ JWT Token: {}" + jwt.getTokenValue()); // Hiển thị token
            logger.debug("⚡ User Roles: {}" + realmRoles);
            Collection<GrantedAuthority> authorities = realmRoles.stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                    .collect(Collectors.toList());

            return Mono.just(new JwtAuthenticationToken(jwt, authorities));
        };
    }
}
