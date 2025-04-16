package com.microservices.notification.user_notification;

public record UserNotificationRequest(
        String id,
        String userId,
        String notificationId,
        String content,
        boolean isRead
) {
}
