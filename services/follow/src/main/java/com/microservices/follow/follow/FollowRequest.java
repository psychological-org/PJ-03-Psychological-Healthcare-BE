package com.microservices.follow.follow;

import jakarta.validation.constraints.NotNull;

public record FollowRequest(
        Integer id,
        @NotNull(message = "Follow content is required") String status,
        @NotNull(message = "Follow communityId is required") Integer senderId,
        @NotNull(message = "Follow userId is required") Integer receiverId) {

}