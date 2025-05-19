package com.microservices.post.user;

public record UserResponse(
        String id, // từ claim sub
        String username, // từ claim preferred_username
        String email, // từ claim email
        String fullName, // từ claim name
        String role, // realm_access.roles
        // extended profile
        String biography,
        String yearOfBirth,
        String yearOfExperience,
        String avatarUrl,
        String backgroundUrl,
        String phone,
        String content) {
}