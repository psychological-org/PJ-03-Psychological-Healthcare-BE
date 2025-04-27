package com.microservices.user.user;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserMapper {

    public User toUser(UserRequest req) {
        if (req == null) return null;
        return User.builder()
                .id(req.id())
                .biography(req.biography())
                .yearOfBirth(req.yearOfBirth())
                .yearOfExperience(req.yearOfExperience())
                .avatarUrl(req.avatarUrl())
                .backgroundUrl(req.backgroundUrl())
                .phone(req.phone())
                .content(req.content())
                .build();
    }

    public UserResponse fillProfile(UserResponse resp, User profile) {
        if (profile == null) {
            return resp;
        }
        return new UserResponse(
                resp.id(),
                resp.username(),
                resp.email(),
                resp.fullName(),
                resp.roles(),
                profile.getBiography(),
                profile.getYearOfBirth(),
                profile.getYearOfExperience(),
                profile.getAvatarUrl(),
                profile.getBackgroundUrl(),
                profile.getPhone(),
                profile.getContent()
        );
    }

    public UserResponse coreToResponse(
            String id, String username, String email, String fullName,
            List<String> roles
    ) {
        return new UserResponse(
                id, username, email, fullName, roles,
                null, null, null, null, null, null, null
        );
    }

}
