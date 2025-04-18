package com.microservices.follow.follow;

public record FollowResponse(
        Integer id,
        String status,
        String senderId,
        String receiverId) {}
