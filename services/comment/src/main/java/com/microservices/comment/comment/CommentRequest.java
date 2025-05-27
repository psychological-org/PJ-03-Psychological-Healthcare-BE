package com.microservices.comment.comment;

public record CommentRequest(
        Integer id,
        String content,
        String imageUrl,
        String userId,
        Integer postId,
        Integer reactCount) {

}