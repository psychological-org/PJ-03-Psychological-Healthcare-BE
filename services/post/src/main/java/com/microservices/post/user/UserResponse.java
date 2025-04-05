package com.microservices.post.user;

public record UserResponse(
                String id,
                String fullName,
                String phone,
                String email) {

}
