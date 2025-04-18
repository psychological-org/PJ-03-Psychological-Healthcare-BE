package com.microservices.notification.notification;

public record NotificationRequest(
        String id,
        String content,
        NotificationType type
) {
}
