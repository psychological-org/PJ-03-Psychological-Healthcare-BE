package com.microservices.comment.like_comment;

import org.springframework.stereotype.Service;

@Service
public class LikeCommentMapper {
    public LikeComment toLikeComment(LikeCommentRequest likeCommentRequest) {
        if (likeCommentRequest == null) {
            return null;
        }
        return LikeComment.builder()
                .id(likeCommentRequest.id())
                .commentId(likeCommentRequest.commentId())
                .userId(likeCommentRequest.userId())
                .build();
    }

    public LikeCommentResponse fromLikeComment(LikeComment likeComment) {
        if (likeComment == null) {
            return null;
        }
        return new LikeCommentResponse(
                likeComment.getId(),
                likeComment.getCommentId(),
                likeComment.getUserId()
        );
    }
}
