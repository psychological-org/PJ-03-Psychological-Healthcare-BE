package com.microservices.post.post;

import jakarta.validation.constraints.NotNull;

public record PostRequest(
        Integer id,
        @NotNull(message = "Post content is required") String content,
        @NotNull(message = "Post communityId is required") Integer communityId,
        @NotNull(message = "Post userId is required") Integer userId) {

}