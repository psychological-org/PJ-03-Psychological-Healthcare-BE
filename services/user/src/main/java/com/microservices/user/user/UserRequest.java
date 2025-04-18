package com.microservices.user.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record UserRequest(
                String id,
                String fullName,
                String biography,
                String yearOfBirth,
                String yearOfExperience,
                String avatarUrl,

                @Email(message="User Email is not a valid email address")
                String email,
                String phone,
                String password,
                String content
                ) {}