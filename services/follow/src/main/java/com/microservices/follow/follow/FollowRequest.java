package com.microservices.follow.follow;

public record FollowRequest(
        Integer id,
        String status,
        String senderId,
        String receiverId) {

}