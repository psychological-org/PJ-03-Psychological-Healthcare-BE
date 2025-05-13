package com.microservices.user.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

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