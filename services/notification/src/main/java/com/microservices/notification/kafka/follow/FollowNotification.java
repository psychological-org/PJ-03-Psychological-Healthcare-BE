package com.microservices.notification.kafka.follow;

import java.time.LocalDateTime;

public record FollowNotification(
        Integer notificationId,
        Integer senderId,
        Integer receiverId,
        LocalDateTime notificationDate) {
}