package com.microservices.comment.comment;

import org.springframework.stereotype.Service;

@Service
public class CommentMapper {

    public Comment toComment(CommentRequest request) {
        if (request == null) {
            return null;
        }
        return Comment.builder()
                .id(request.id())
                .content(request.content())
                .imageUrl(request.imageUrl())
                .userId(request.userId())
                .postId(request.postId())
                .build();
    }

    public CommentResponse fromComment(Comment comment) {
        if (comment == null) {
            return null;
        }
        return new CommentResponse(
                comment.getId(),
                comment.getContent(),
                comment.getImageUrl(),
                comment.getReactCount(),
                comment.getUserId(),
                comment.getPostId());
    }

}
