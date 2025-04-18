package com.microservices.community.community;

import jakarta.validation.constraints.NotNull;

public record CommunityRequest(
                Integer id,
                String name,
                String content,
                String avatarUrl,
                String adminId) {

}