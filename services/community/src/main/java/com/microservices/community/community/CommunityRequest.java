package com.microservices.community.community;

import jakarta.validation.constraints.NotNull;

public record CommunityRequest(
                Integer id,
                @NotNull(message = "Community name is required") String name,
                @NotNull(message = "Community content is required") String content,
                @NotNull(message = "Community avatarUrl is required") String avatarUrl,
                @NotNull(message = "Community adminId is required") String adminId) {

}