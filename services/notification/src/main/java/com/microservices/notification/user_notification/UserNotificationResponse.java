package com.microservices.notification.user_notification;

public record UserNotificationResponse(
        String id,
        String userId,
        String notificationId,
        String content,
        boolean isRead
) {
}
