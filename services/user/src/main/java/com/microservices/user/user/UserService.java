package com.microservices.user.user;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.microservices.user.utils.PagedResponse;
import org.apache.catalina.security.SecurityConfig;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.microservices.user.exception.UserNotFoundException;

import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repo;
    private final UserMapper mapper;

    private final Keycloak keycloak;
    @Value("${keycloak.realm}") private String realm;

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(UserService.class);

    private RealmResource realmResource() {
        return keycloak.realm(realm);
    }

    public String createUser(UserRequest request) {
        var user = this.repo.save(mapper.toUser(request));
        return user.getId();
    }

    public void updateUser(UserRequest request) {
        var user = this.repo.findById(request.id())
                .orElseThrow(() -> new UserNotFoundException(
                        String.format("Cannot update User:: No User found with the provided ID: %s",
                                request.id())));
        mergeUser(user, request);
        this.repo.save(user);
    }

    private void mergeUser(User user, UserRequest request) {
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
        if (StringUtils.isNotBlank(request.backgroundUrl())) {
            user.setBackgroundUrl(request.backgroundUrl());
        }
        if (StringUtils.isNotBlank(request.content())) {
            user.setContent(request.content());
        }
    }

//    public List<UserResponse> findAllUsers() {
//        return this.repository.findAll()
//                .stream()
//                .map(this.mapper::fromUser)
//                .collect(Collectors.toList());
//    }

//    public PagedResponse<UserResponse> findAllUsers(int page, int limit) {
//        Pageable pageable = PageRequest.of(page, limit);
//        Page<User> appointments = this.repository.findAllCollections(pageable);
//        if (appointments.getContent().isEmpty()) {
//            throw new UserNotFoundException("No topic found");
//        }
//        List<UserResponse> appointmentResponses = appointments.getContent()
//                .stream()
//                .map(this.mapper::fromUser)
//                .collect(Collectors.toList());
//        return new PagedResponse<>(appointmentResponses, appointments.getTotalPages(), appointments.getTotalElements());
//    }

    public Page<UserResponse> findAllUsers(int page, int size) {
        // 1. Lấy danh sách core user paged từ Keycloak
        int first = page * size;
        List<UserRepresentation> kcUsers = realmResource()
                .users().list(first, size);
        long total = realmResource().users().count();

        // 2. Map từng phần tử
        List<UserResponse> dtos = kcUsers.stream().map(u -> {
            // core
            UserResponse resp = mapper.coreToResponse(
                    u.getId(), u.getUsername(), u.getEmail(),
                    u.getFirstName() + " " + u.getLastName(),
                    u.getRealmRoles()
            );
            // profile
            repo.findById(u.getId())
                    .ifPresent(profile -> mapper.fillProfile(resp, profile));
            return resp;
        }).collect(Collectors.toList());

        return new PageImpl<>(dtos, PageRequest.of(page, size), total);
    }


//    public UserResponse findById(String id) {
//        return this.repository.findById(id)
//                .map(mapper::fromUser)
//                .orElseThrow(() -> new UserNotFoundException(
//                        String.format("No User found with the provided ID: %s", id)));
//    }


    public UserResponse findById(String id) {
        logger.debug("⚡ Finding user by ID: {}", id);

        // 1. Fetch UserRepresentation từ Keycloak
        UserRepresentation userRepresentation;
        try {
            userRepresentation = realmResource().users().get(id).toRepresentation();
        } catch (Exception ex) {
            logger.error("Failed to fetch user from Keycloak for ID: {}", id, ex);
            throw new UserNotFoundException(String.format("No User found in Keycloak with ID: %s", id));
        }

        if (userRepresentation == null) {
            logger.warn("⚠ No UserRepresentation found for ID: {}", id);
            throw new UserNotFoundException(String.format("No User found with ID: %s", id));
        }

        // 2. Fetch thêm Realm Roles
        List<String> realmRoles;
        try {
            realmRoles = realmResource()
                    .users()
                    .get(id)
                    .roles()
                    .realmLevel()
                    .listAll()
                    .stream()
                    .map(role -> role.getName())
                    .collect(Collectors.toList());
            System.out.println("Realm Roles: " + realmRoles);
        } catch (Exception e) {
            logger.warn("⚠ Failed to fetch roles for user ID: {}", id, e);
            realmRoles = null; // fallback gracefully
        }

        // 3. Map dữ liệu core
        String fullName = String.format("%s %s",
                userRepresentation.getFirstName() != null ? userRepresentation.getFirstName() : "",
                userRepresentation.getLastName() != null ? userRepresentation.getLastName() : ""
        ).trim();
        if (fullName.isEmpty()) {
            fullName = null;
        }

        UserResponse response = mapper.coreToResponse(
                userRepresentation.getId(),
                userRepresentation.getUsername(),
                userRepresentation.getEmail(),
                fullName,
                realmRoles
        );

        // 4. Nếu MongoDB có profile, fill thêm
        return repo.findById(id)
                .map(profile -> {
                    logger.debug("✅ Found additional profile data in MongoDB for user ID: {}", id);
                    return mapper.fillProfile(response, profile);
                })
                .orElse(response);
    }

    public void deleteUser(String id) {
        this.repo.deleteById(id);
    }
}
