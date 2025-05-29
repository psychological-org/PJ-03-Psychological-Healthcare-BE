package com.microservices.post.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.*;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "/api/v1/posts/**").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        // Chỉ bật JWT Resource Server, dùng converter mặc định
                        .jwt(Customizer.withDefaults())
                );
//                .oauth2ResourceServer(oauth2 -> oauth2
//                        .jwt(jwt -> jwt
//                                .jwtAuthenticationConverter(grantedAuthoritiesExtractor())
//                        )

        return http.build();
    }

//    // <-- đây là non-reactive converter, trả luôn AbstractAuthenticationToken
//    private Converter<Jwt, AbstractAuthenticationToken> grantedAuthoritiesExtractor() {
//        return jwt -> {
//            @SuppressWarnings("unchecked")
//            List<String> realmRoles = jwt.getClaim("realm_access") != null
//                    ? ((List<String>) ((Map<String, Object>) jwt.getClaim("realm_access")).get("roles"))
//                    : List.of();
//
//            Collection<GrantedAuthority> authorities = realmRoles.stream()
//                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
//                    .collect(Collectors.toList());
//
//            return new JwtAuthenticationToken(jwt, authorities);
//        };
//    }
}

