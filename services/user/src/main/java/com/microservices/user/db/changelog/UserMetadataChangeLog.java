package com.microservices.user.db.changelog;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.microservices.user.seeder.KeycloakUserSeeder;
import com.microservices.user.user.User;
import com.microservices.user.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@ChangeLog(order = "001")
@RequiredArgsConstructor
public class UserMetadataChangeLog {

    private final UserRepository userRepository;

    @ChangeSet(order = "001", id = "seedUserMetadata", author = "loantuyet")
    public void seedMetadata(MongoTemplate mongoTemplate) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        InputStream is = new ClassPathResource("seed/users.json").getInputStream();
        List<Map<String, Object>> users = mapper.readValue(is, new TypeReference<>() {});
        Map<String, String> idMap = KeycloakUserSeeder.getUserIdMap();

        for (Map<String, Object> user : users) {
            String username = (String) user.get("username");
            String keycloakId = idMap.get(username);
            String hardcodedMongoId = (String) user.get("id");

            if (keycloakId == null || hardcodedMongoId == null) continue;

            if (userRepository.findById(hardcodedMongoId).isPresent()) {
                System.out.println("⚠️ Skipping: Metadata for user " + username + " already exists.");
                continue;
            }

            Map<String, Object> metadata = (Map<String, Object>) user.get("metadata");
            if (metadata == null) continue;

            User entity = User.builder()
                    .id(hardcodedMongoId)
                    .keycloakId(keycloakId)
                    .biography((String) metadata.get("biography"))
                    .yearOfBirth((String) metadata.get("yearOfBirth"))
                    .yearOfExperience((String) metadata.get("yearOfExperience"))
                    .avatarUrl((String) metadata.get("avatarUrl"))
                    .backgroundUrl((String) metadata.get("backgroundUrl"))
                    .phone((String) metadata.get("phone"))
                    .content((String) metadata.get("content"))
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            userRepository.save(entity);
            System.out.println("✅ Inserted metadata for user: " + username);
        }
    }
}
