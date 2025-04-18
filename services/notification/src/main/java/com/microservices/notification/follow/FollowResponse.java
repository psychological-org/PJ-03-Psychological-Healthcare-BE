package com.microservices.notification.follow;

public record FollowResponse(
        Integer id,
        String status,
        String senderId,
        String receiverId
) {
}
