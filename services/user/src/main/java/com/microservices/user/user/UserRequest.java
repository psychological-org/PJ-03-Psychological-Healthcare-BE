package com.microservices.user.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record UserRequest(
                String id,
                @NotNull(message="User fullname is required")String fullName,
                @NotNull(message="User phone is required")String phone,
                @NotNull(message="User yearOfBirth is required")String yearOfBirth,
                @NotNull(message="User Email is required")@Email(message="User Email is not a valid email address")
                String email,
                @NotNull(message="User password is required")
                String password
                ) {

}