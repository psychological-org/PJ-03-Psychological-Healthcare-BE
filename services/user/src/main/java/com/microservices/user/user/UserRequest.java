package com.microservices.user.user;

public record UserRequest(
                String id,
                String keycloakId,
                String username,
                String password,
                String email,
                String firstName,
                String lastName,
                String role,
                String biography,
                String yearOfBirth,
                String yearOfExperience,
                String avatarUrl,
                String backgroundUrl,
                String phone,
                String content
                ) {}

