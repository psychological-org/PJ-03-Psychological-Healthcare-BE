package com.microservices.user.user;

public record UserRequest(
                String id,
                String keycloakId,
                String biography,
                String yearOfBirth,
                String yearOfExperience,
                String avatarUrl,
                String backgroundUrl,
                String phone,
                String content
                ) {}