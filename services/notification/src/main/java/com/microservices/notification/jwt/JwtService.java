package com.microservices.notification.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

import static org.apache.kafka.common.requests.DeleteAclsResponse.log;

@Service
public class JwtService {

    @Value("${jwk-set-uri}")
    private String jwkSetUri;

    private JwtDecoder jwtDecoder;

    @PostConstruct
    public void init() {
        // Khởi tạo JwtDecoder với JWK Set URI
        this.jwtDecoder = NimbusJwtDecoder.withJwkSetUri(jwkSetUri)
                .jwsAlgorithm(SignatureAlgorithm.RS256)
                .build();
    }

    public String extractUserId(String token) {
        try {
            var jwt = jwtDecoder.decode(token);
            return jwt.getClaimAsString("sub");
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid JWT token: " + e.getMessage());
        }
    }

    public String extractRole(String token) {
        try {
            var jwt = jwtDecoder.decode(token);
            var realmAccess = jwt.getClaimAsMap("realm_access");
            if (realmAccess != null) {
                List<String> roles = (List<String>) realmAccess.get("roles");
                if (roles != null && !roles.isEmpty()) {
                    return roles.stream()
                            .filter(role -> role.equals("patient") || role.equals("doctor"))
                            .findFirst()
                            .orElseGet(() -> {
                                log.warn("No valid role (patient or doctor) found in token roles: {}", roles);
                                throw new IllegalArgumentException("No valid role (patient or doctor) found in token");
                            });
                }
            }
            log.error("No realm_access or roles found in token: {}", jwt.getClaims());
            throw new IllegalArgumentException("No roles found in token");
        } catch (Exception e) {
            log.error("Invalid JWT token: {}", e.getMessage(), e);
            throw new IllegalArgumentException("Invalid JWT token: " + e.getMessage());
        }
    }
}
