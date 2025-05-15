package com.microservices.user.seeder;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservices.user.user.User;
import com.microservices.user.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.util.*;

@RequiredArgsConstructor
@Component
public class KeycloakUserSeeder implements CommandLineRunner {

    private final Keycloak keycloakAdmin;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${keycloak.admin.realm}")
    private String realm;

    @Value("${keycloak.admin.server-url}")
    private String serverUrl;

    @Value("${keycloak.admin.client-id}")
    private String clientId;

    @Value("${keycloak.admin.client-secret}")
    private String clientSecret;

    private static final Map<String, String> userIdMap = new HashMap<>();

    public static Map<String, String> getUserIdMap() {
        return userIdMap;
    }

    @Override
    public void run(String... args) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        InputStream is = new ClassPathResource("seed/users.json").getInputStream();
        List<Map<String, Object>> users = mapper.readValue(is, new TypeReference<>() {});

        String accessToken = getAdminAccessToken();

        for (Map<String, Object> user : users) {
            String username = (String) user.get("username");
            Map<String, Object> metadata = (Map<String, Object>) user.get("metadata");

            List<UserRepresentation> existing = keycloakAdmin.realm(realm).users().search(username, true);
            if (!existing.isEmpty()) {
                String existingId = existing.get(0).getId();
                userIdMap.put(username, existingId);
                System.out.println("⚠️ User " + username + " already exists, skipping...");
                continue;
            }

            user.remove("metadata");

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(user, headers);
            ResponseEntity<Void> response = restTemplate.exchange(
                    serverUrl + "/admin/realms/" + realm + "/users",
                    HttpMethod.POST,
                    request,
                    Void.class
            );

            if (response.getStatusCode() != HttpStatus.CREATED) {
                throw new RuntimeException("❌ Failed to create user: " + username);
            }

            Thread.sleep(500);

            List<UserRepresentation> created = keycloakAdmin.realm(realm).users().search(username, true);
            String createdId = created.get(0).getId();

            List<Map<String, Object>> credentials = (List<Map<String, Object>>) user.get("credentials");
            Map<String, Object> credential = credentials.get(0);

            CredentialRepresentation passwordRep = new CredentialRepresentation();
            passwordRep.setType("password");
            passwordRep.setTemporary((Boolean) credential.getOrDefault("temporary", false));
            passwordRep.setValue((String) credential.get("value"));

            keycloakAdmin.realm(realm).users().get(createdId).resetPassword(passwordRep);

            // 🔁 Insert matching user profile in MongoDB with keycloakId as FK
            User profile = User.builder()
                    .id((String) user.get("id"))
                    .keycloakId(createdId)
                    .biography((String) metadata.get("biography"))
                    .yearOfBirth((String) metadata.get("yearOfBirth"))
                    .yearOfExperience((String) metadata.get("yearOfExperience"))
                    .avatarUrl((String) metadata.get("avatarUrl"))
                    .backgroundUrl((String) metadata.get("backgroundUrl"))
                    .phone((String) metadata.get("phone"))
                    .content((String) metadata.get("content"))
                    .build();

            userRepository.save(profile);
            userIdMap.put(username, createdId);
            System.out.println("✅ Created Keycloak + Mongo user: " + username + " (Keycloak ID: " + createdId + ")");
        }
    }

    private String getAdminAccessToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "client_credentials");
        form.add("client_id", clientId);
        form.add("client_secret", clientSecret);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(form, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                serverUrl + "/realms/" + realm + "/protocol/openid-connect/token",
                HttpMethod.POST,
                request,
                Map.class
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("❌ Failed to obtain access token: " + response.getStatusCode());
        }

        return (String) response.getBody().get("access_token");
    }
}
