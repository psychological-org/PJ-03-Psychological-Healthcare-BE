package com.microservices.post.like_post;

import org.springframework.stereotype.Service;

@Service
public class LikePostMapper {
    public LikePost toListPost(LikePostRequest likePostRequest) {
        if (likePostRequest == null) {
            return null;
        }
        return LikePost.builder()
                .id(likePostRequest.id())
                .postId(likePostRequest.postId())
                .userId(likePostRequest.userId())
                .build();
    }
    public LikePostResponse fromLikePost(LikePost likePost) {
        if (likePost == null) {
            return null;
        }
        return new LikePostResponse(
                likePost.getId(),
                likePost.getPostId(),
                likePost.getUserId()
        );
    }
}
