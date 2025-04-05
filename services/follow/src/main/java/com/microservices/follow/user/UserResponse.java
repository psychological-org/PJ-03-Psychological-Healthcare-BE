package com.microservices.follow.user;

public record UserResponse(
                String id,
                String fullName,
                String phone,
                String email) {

}
