package com.microservices.notification.kafka.post;

public record PostNotification(
        Integer id,
        String content,
        String imageUrl,
        String visibility,
        Integer reactCount,
        Integer communityId,
        String userId) {
}