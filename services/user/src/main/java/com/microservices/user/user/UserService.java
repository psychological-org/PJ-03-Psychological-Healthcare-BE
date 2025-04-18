package com.microservices.user.user;

import java.util.List;
import java.util.stream.Collectors;

import com.microservices.user.utils.PagedResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.microservices.user.exception.UserNotFoundException;

import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final UserMapper mapper;

    public String createUser(UserRequest request) {
        var user = this.repository.save(mapper.toUser(request));
        return user.getId();
    }

    public void updateUser(UserRequest request) {
        var user = this.repository.findById(request.id())
                .orElseThrow(() -> new UserNotFoundException(
                        String.format("Cannot update User:: No User found with the provided ID: %s",
                                request.id())));
        mergeUser(user, request);
        this.repository.save(user);
    }

    private void mergeUser(User user, UserRequest request) {
        if (StringUtils.isNotBlank(request.fullName())) {
            user.setFullName(request.fullName());
        }
        if (StringUtils.isNotBlank(request.email())) {
            user.setEmail(request.email());
        }
        if (request.phone() != null) {
            user.setPhone(request.phone());
        }
        if (StringUtils.isNotBlank(request.biography())) {
            user.setBiography(request.biography());
        }
        if (StringUtils.isNotBlank(request.yearOfBirth())) {
            user.setYearOfBirth(request.yearOfBirth());
        }
        if (StringUtils.isNotBlank(request.yearOfExperience())) {
            user.setYearOfExperience(request.yearOfExperience());
        }
        if (StringUtils.isNotBlank(request.avatarUrl())) {
            user.setAvatarUrl(request.avatarUrl());
        }
        if (StringUtils.isNotBlank(request.content())) {
            user.setContent(request.content());
        }
        if (StringUtils.isNotBlank(request.password())) {
            user.setPassword(request.password());
        }
    }

//    public List<UserResponse> findAllUsers() {
//        return this.repository.findAll()
//                .stream()
//                .map(this.mapper::fromUser)
//                .collect(Collectors.toList());
//    }

    public PagedResponse<UserResponse> findAllUsers(int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit);
        Page<User> appointments = this.repository.findAllCollections(pageable);
        if (appointments.getContent().isEmpty()) {
            throw new UserNotFoundException("No topic found");
        }
        List<UserResponse> appointmentResponses = appointments.getContent()
                .stream()
                .map(this.mapper::fromUser)
                .collect(Collectors.toList());
        return new PagedResponse<>(appointmentResponses, appointments.getTotalPages(), appointments.getTotalElements());
    }

    public UserResponse findById(String id) {
        return this.repository.findById(id)
                .map(mapper::fromUser)
                .orElseThrow(() -> new UserNotFoundException(
                        String.format("No User found with the provided ID: %s", id)));
    }

    public boolean existsById(String id) {
        return this.repository.findById(id)
                .isPresent();
    }

    public void deleteUser(String id) {
        this.repository.deleteById(id);
    }
}
