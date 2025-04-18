package com.microservices.notification.user;

public record UserResponse(
        String id,
        String fullName,
        String biography,
        String yearOfBirth,
        String yearOfExperience,
        String avatarUrl,
        String email,
        String phone,
        String password,
        String content) {

}

