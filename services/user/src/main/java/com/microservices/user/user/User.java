package com.microservices.user.user;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Document(collection = "user")
public class User {

    @Id
    private String id;
    private String fullName;
    private String biography;
    private String yearOfBirth;
    private String yearOfExperience;
    private String avatarUrl;
    private String backgroundUrl;
    private String email;
    private String phone;
    private String password;
    private String content;
    private String createdAt;
    private String updatedAt;
    private String deletedAt;
}