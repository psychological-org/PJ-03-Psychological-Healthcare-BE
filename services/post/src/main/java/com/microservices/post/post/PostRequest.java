package com.microservices.post.post;

import jakarta.validation.constraints.NotNull;

public record PostRequest(
        Integer id,
        String content,
        String imageUrl,
        String visibility,
        Integer reactCount,
        Integer communityId,
        String userId) {

}