package com.microservices.attendance.community;

public record CommunityResponse(
        Integer id,
        String name,
        String content,
        String avatarUrl) {
}
