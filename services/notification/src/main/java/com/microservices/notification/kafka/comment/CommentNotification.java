package com.microservices.notification.kafka.comment;

public record CommentNotification(
                Integer notificationId,
                Integer senderId,
                Integer receiverId) {

}