package com.microservices.post.post;

public record PostResponse(
        Integer id,
        String content,
        String imageUrl,
        String visibility,
        Integer reactCount,
        Integer communityId,
        String userId) {

}
