package com.microservices.notification.notification;

public record NotificationResponse(
        String id,
        String content,
        NotificationType type
) {
}
