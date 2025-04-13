package com.microservices.comment.comment;

import jakarta.validation.constraints.NotNull;

public record CommentRequest(
        Integer id,
        String content,
        String imageUrl,
        String userId,
        Integer postId,
        Integer reactCount) {

}