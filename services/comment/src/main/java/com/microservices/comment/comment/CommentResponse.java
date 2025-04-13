package com.microservices.comment.comment;

public record CommentResponse(
                Integer id,
                String content,
                String imageUrl,
                String userId,
                Integer postId,
                Integer reactCount) {

}
