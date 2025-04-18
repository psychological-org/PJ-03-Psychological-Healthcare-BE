package com.microservices.follow.follow;

import jakarta.validation.constraints.NotNull;

public record FollowRequest(
        Integer id,
        String status,
        String senderId,
        String receiverId) {

}