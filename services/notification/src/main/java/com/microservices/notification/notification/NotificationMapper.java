package com.microservices.notification.notification;

import org.springframework.stereotype.Service;

@Service
public class NotificationMapper {
    public Notification toNotification(NotificationRequest notificationRequest) {
        if (notificationRequest == null) {
            return null;
        }
        return Notification.builder()
                .id(notificationRequest.id())
                .content(notificationRequest.content())
                .type(notificationRequest.type())
                .build();
    }

    public NotificationResponse fromNotification(Notification notification) {
        if (notification == null) {
            return null;
        }
        return new NotificationResponse(
                notification.getId(),
                notification.getContent(),
                notification.getType()
        );
    }
}
