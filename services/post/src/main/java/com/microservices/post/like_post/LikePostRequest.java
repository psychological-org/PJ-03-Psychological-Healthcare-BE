package com.microservices.post.like_post;

public record LikePostRequest(
        Integer id,
        Integer postId,
        String userId
) {
    public String toString() {
        return "LikePostServiceRequest{" +
                "id=" + id +
                ", postId=" + postId +
                ", userId='" + userId + '\'' +
                '}';
    }
}
