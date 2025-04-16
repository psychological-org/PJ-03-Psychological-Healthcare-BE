package com.microservices.notification.kafka.follow;

import java.time.LocalDateTime;

public record FollowNotification(
        Integer id,
        String status,
        String senderId,
        String receiverId) {

    public String toString() {
        return "FollowNotification{" +
                "id=" + id +
                ", status='" + status + '\'' +
                ", senderId='" + senderId + '\'' +
                ", receiverId='" + receiverId + '\'' +
                '}';
    }
}