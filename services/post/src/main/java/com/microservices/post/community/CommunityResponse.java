package com.microservices.post.community;

public record CommunityResponse(
        Integer id,
        String name,
        String content,
        String avatarUrl,
        String adminId) {
}
