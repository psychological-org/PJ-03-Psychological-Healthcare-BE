package com.microservices.comment.post;

public record PostResponse(
        Integer id,
        String content,
        String imageUrl,
        String visibility,
        Integer reactCount,
        Integer communityId,
        Integer userId) {

}
