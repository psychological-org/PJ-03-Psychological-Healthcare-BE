package com.microservices.community.user;

public record UserResponse(
                String id,
                String fullName,
                String phone,
                String email) {

}
