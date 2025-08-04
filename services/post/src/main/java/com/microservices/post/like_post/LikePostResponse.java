package com.microservices.post.like_post;

public record LikePostResponse(
        Integer id,
        Integer postId,
        String userId
) {
    public String toString() {
        return "LikePostServiceResponse{" +
                "id=" + id +
                ", postId=" + postId +
                ", userId='" + userId + '\'' +
                '}';
    }
}
