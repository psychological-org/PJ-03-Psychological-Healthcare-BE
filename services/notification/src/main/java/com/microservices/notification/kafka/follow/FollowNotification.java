package com.microservices.notification.kafka.follow;

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