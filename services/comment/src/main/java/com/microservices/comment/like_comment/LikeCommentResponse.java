package com.microservices.comment.like_comment;

public record LikeCommentResponse(
        Integer id,
        Integer commentId,
        String userId
) {
    public String toString() {
        return "LikePostServiceResponse{" +
                "id=" + id +
                ", commentId=" + commentId +
                ", userId='" + userId + '\'' +
                '}';
    }
}
