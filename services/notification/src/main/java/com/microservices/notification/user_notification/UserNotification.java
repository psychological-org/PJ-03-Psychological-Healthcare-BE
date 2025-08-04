package com.microservices.notification.user_notification;

import lombok.*;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.mapping.Document;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@EnableMongoAuditing
@Document(collection = "user_notification")
public class UserNotification {
    private String id;
    private String userId;
    private String notificationId;
    private String content;
    private boolean isRead;

    public String toString() {
        return "UserNotification: " + id;
    }
}
