package com.microservices.comment.user;

public record UserResponse(
                String id,
                String fullName,
                String phone,
                String email) {

}
