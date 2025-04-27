package com.microservices.user.config;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// KeycloakAdminConfig.java
@Configuration
public class KeycloakAdminConfig {
    @Bean
    public Keycloak keycloakAdmin(
            @Value("${keycloak.admin.server-url}") String serverUrl,
            @Value("${keycloak.admin.realm}")       String realmMaster,
            @Value("${keycloak.admin.client-id}")   String clientId,
            @Value("${keycloak.admin.username}")    String username,
            @Value("${keycloak.admin.password}")    String password,
            @Value("${keycloak.admin.client-secret}") String clientSecret
    ) {
        System.out.println("KeycloakAdminConfig.keycloakAdmin");
        System.out.println("serverUrl = " + serverUrl);
        System.out.println("realmMaster = " + realmMaster);
        System.out.println("clientId = " + clientId);
        System.out.println("username = " + username);
        System.out.println("password = " + password);
        System.out.println("clientSecret = " + clientSecret);

        return KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(realmMaster)
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)  // BẮT BUỘC PHẢI client_credentials
                .clientId(clientId)
                .clientSecret(clientSecret)
                .build();

//        return KeycloakBuilder.builder()
//                .serverUrl(serverUrl)
//                .realm("micro-services")
//                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
//                .clientId(clientId)
//                .clientSecret(clientSecret)
//                .build();

    }
}

