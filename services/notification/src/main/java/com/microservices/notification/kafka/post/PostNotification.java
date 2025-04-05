package com.microservices.notification.kafka.post;

public record PostNotification(
                Integer notificationId,
                Integer senderId,
                Integer receiverId) {
}