package com.microservices.notification.user_notification;

import org.springframework.stereotype.Service;

@Service
public class UserNotificationMapper {
    public UserNotification toUserNotification(UserNotificationRequest userNotificationRequest) {
        if (userNotificationRequest == null) {
            return null;
        }
        return UserNotification.builder()
                .id(userNotificationRequest.id())
                .userId(userNotificationRequest.userId())
                .notificationId(userNotificationRequest.notificationId())
                .content(userNotificationRequest.content())
                .isRead(userNotificationRequest.isRead())
                .build();
    }

    public UserNotificationResponse fromUserNotification(UserNotification userNotification) {
        if (userNotification == null) {
            return null;
        }
        return new UserNotificationResponse(
                userNotification.getId(),
                userNotification.getUserId(),
                userNotification.getNotificationId(),
                userNotification.getContent(),
                userNotification.isRead()
        );
    }
}
