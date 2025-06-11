package com.microservices.comment.comment;

import java.time.LocalDateTime;

public record CommentResponse(
        Integer id,
        String content,
        String imageUrl,
        String userId,
        Integer postId,
        Integer reactCount,
        LocalDateTime createdAt) {

}
