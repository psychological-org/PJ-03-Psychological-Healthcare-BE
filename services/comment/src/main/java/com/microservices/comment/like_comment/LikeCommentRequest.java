package com.microservices.comment.like_comment;

public record LikeCommentRequest(
        Integer id,
        Integer commentId,
        String userId
) {
    public String toString() {
        return "LikeCommentServiceRequest{" +
                "id=" + id +
                ", commentId=" + commentId +
                ", userId='" + userId + '\'' +
                '}';
    }
}
