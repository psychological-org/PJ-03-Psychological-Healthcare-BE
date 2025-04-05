package com.microservices.appointment.user;

public record UserResponse(
        String id,
        String fullName,
        String phone,
        String email) {

}
