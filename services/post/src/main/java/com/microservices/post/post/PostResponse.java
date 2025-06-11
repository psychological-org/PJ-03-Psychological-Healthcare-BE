package com.microservices.post.post;

import java.time.LocalDateTime;

public record PostResponse(
        Integer id,
        String content,
        String imageUrl,
        String visibility,
        Integer reactCount,
        Integer communityId,
        String userId,
        LocalDateTime createdAt) {

}
