package com.microservices.user.user;

import jakarta.validation.constraints.Email;

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
        String content
        ) {}
