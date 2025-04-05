package com.microservices.comment.comment;

import jakarta.validation.constraints.NotNull;

public record CommentRequest(
        Integer id,
        @NotNull(message = "Comment content is required") String content,
        @NotNull(message = "Comment imageUrl is required") String imageUrl,
        @NotNull(message = "Comment adminId is required") Integer userId,
        @NotNull(message = "Comment adminId is required") Integer postId) {

}