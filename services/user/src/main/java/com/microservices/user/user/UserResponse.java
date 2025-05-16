//package com.microservices.user.user;
//
//import jakarta.validation.constraints.Email;
//
//public record UserResponse(
//        String id,
//        String fullName,
//        String biography,
//        String yearOfBirth,
//        String yearOfExperience,
//        String avatarUrl,
//        String email,
//        String phone,
//        String password,
//        String content,
//        String username,
//        String role
//        ) {}

package com.microservices.user.user;

import java.util.List;

public record UserResponse(
        String id,                // từ claim sub
        String username,          // từ claim preferred_username
        String email,             // từ claim email
        String fullName,          // từ claim name
        List<String> roles,       // realm_access.roles
        // extended profile
        String biography,
        String yearOfBirth,
        String yearOfExperience,
        String avatarUrl,
        String backgroundUrl,
        String phone,
        String content
) {}

