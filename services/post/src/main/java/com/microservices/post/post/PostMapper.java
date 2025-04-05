package com.microservices.post.post;

import org.springframework.stereotype.Service;

@Service
public class PostMapper {

    public Post toPost(PostRequest request) {
        if (request == null) {
            return null;
        }
        return Post.builder()
                .id(request.id())
                .content(request.content())
                .communityId(request.communityId())
                .userId(request.userId())
                .build();
    }

    public PostResponse fromPost(Post post) {
        if (post == null) {
            return null;
        }
        return new PostResponse(
                post.getId(),
                post.getContent(),
                post.getImageUrl(),
                post.getVisibility(),
                post.getReactCount(),
                post.getCommunityId(),
                post.getUserId());

    }

}
