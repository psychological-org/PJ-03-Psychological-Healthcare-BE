package com.microservices.user.user;

import org.springframework.stereotype.Service;

@Service
public class UserMapper {

    public User toUser(UserRequest request) {
        if (request == null) {
            return null;
        }
        return User.builder()
                .id(request.id())
                .fullName(request.fullName())
                .biography(request.biography())
                .yearOfBirth(request.yearOfBirth())
                .yearOfExperience(request.yearOfExperience())
                .avatarUrl(request.avatarUrl())
                .email(request.email())
                .phone(request.phone())
                .password(request.password())
                .content(request.content())
                .build();
    }

    public UserResponse fromUser(User user) {
        if (user == null) {
            return null;
        }
        return new UserResponse(
                user.getId(),
                user.getFullName(),
                user.getBiography(),
                user.getYearOfBirth(),
                user.getYearOfExperience(),
                user.getAvatarUrl(),
                user.getEmail(),
                user.getPhone(),
                user.getPassword(),
                user.getContent()
        );
    }

}
