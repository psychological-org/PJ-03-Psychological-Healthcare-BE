package com.microservices.user.user;

import java.util.List;
import java.util.stream.Collectors;

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
    }

    public List<UserResponse> findAllUsers() {
        return this.repository.findAll()
                .stream()
                .map(this.mapper::fromUser)
                .collect(Collectors.toList());
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
