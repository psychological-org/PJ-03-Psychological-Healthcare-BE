package com.microservices.comment.comment;

public record CommentResponse(
                Integer id,
                String content,
                String imageUrl,
                Integer reactCount,
                Integer userId,
                Integer postId) {

}
