package com.microservices.user.user;

public record UserResponse(
        String id,
        String fullName,
        String phone,
        String email) {

}
