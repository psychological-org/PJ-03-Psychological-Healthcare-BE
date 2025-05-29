package com.microservices.community.community;

public record CommunityRequest(
                Integer id,
                String name,
                String content,
                String avatarUrl,
                String adminId) {

}