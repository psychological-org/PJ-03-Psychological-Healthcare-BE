package com.microservices.follow.follow;

public record FollowResponse(
                Integer id,
                String status,
                Integer senderId,
                Integer receiverId) {

}
