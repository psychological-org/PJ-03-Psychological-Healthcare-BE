package com.microservices.notification.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Configuration
@Slf4j
public class FirebaseConfig {

    @Bean
    public FirebaseApp firebaseApp() {
        try (InputStream serviceAccount = this.getClass().getResourceAsStream("/service-account-file.json")) {
            if (serviceAccount == null) {
                log.error("Service account file not found in classpath: /service-account-file.json");
                throw new IllegalStateException("Service account file not found");
            }
            // Đọc nội dung file JSON
            String jsonContent = new String(serviceAccount.readAllBytes(), StandardCharsets.UTF_8);
            log.info("Service account JSON content: {}", jsonContent.replaceAll("private_key\": \".*?\"", "private_key\": \"[REDACTED]\""));

            // Phân tích JSON thủ công
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(jsonContent);
            String projectIdFromJson = jsonNode.get("project_id").asText();
            log.info("Project ID from JSON parsing: {}", projectIdFromJson);
            if (projectIdFromJson == null || projectIdFromJson.isEmpty()) {
                log.error("Project ID is missing or empty in service account file");
                throw new IllegalStateException("Project ID is missing in service account file");
            }

            GoogleCredentials credentials = GoogleCredentials.fromStream(
                    new ByteArrayInputStream(jsonContent.getBytes(StandardCharsets.UTF_8)));

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(credentials)
                    .setProjectId(projectIdFromJson)
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp app = FirebaseApp.initializeApp(options);
                log.info("FirebaseApp initialized: {}", app.getName());
                return app;
            }
            return FirebaseApp.getInstance();
        } catch (Exception e) {
            log.error("Cannot initialize Firebase", e);
            throw new IllegalStateException("Firebase initialization failed", e);
        }
    }

    @Bean
    public FirebaseMessaging firebaseMessaging(FirebaseApp app) {
        return FirebaseMessaging.getInstance(app);
    }
}
