package com.microservices.user.user;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
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
    @Value("${keycloak.realm}")
    private String realm;

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(UserService.class);

    private RealmResource realmResource() {
        return keycloak.realm(realm);
    }

    public String createUser(UserRequest request) {
        if (request.password() == null || request.password().isBlank()) {
            throw new IllegalArgumentException("Password must not be null or empty");
        }

        if (!List.of("admin", "doctor", "patient").contains(request.role())) {
            throw new IllegalArgumentException("Invalid role: " + request.role());
        }

        UserRepresentation userRep = new UserRepresentation();
        userRep.setUsername(request.username());
        userRep.setEmail(request.email());
        userRep.setFirstName(request.firstName());
        userRep.setLastName(request.lastName());
        userRep.setEnabled(true);
        userRep.setEmailVerified(true);

        String keycloakId;
        try (Response response = realmResource().users().create(userRep)) {
            if (response.getStatus() != 201) {
                throw new RuntimeException("Failed to create user in Keycloak: " + response.getStatusInfo());
            }
            keycloakId = response.getHeaderString("Location")
                    .substring(response.getHeaderString("Location").lastIndexOf('/') + 1);
        }

        CredentialRepresentation passwordCred = new CredentialRepresentation();
        passwordCred.setTemporary(false);
        passwordCred.setType(CredentialRepresentation.PASSWORD);
        passwordCred.setValue(request.password());

        try {
            realmResource().users().get(keycloakId).resetPassword(passwordCred);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set password for user", e);
        }

        RoleRepresentation roleRep = realmResource().roles().get(request.role()).toRepresentation();
        realmResource().users().get(keycloakId).roles().realmLevel().add(List.of(roleRep));

        User user = mapper.toUser(request);
        user.setKeycloakId(keycloakId);
        repo.save(user);

        return user.getId();
    }

    public void updateUser(UserRequest request) {
        // 1. Kiểm tra user MongoDB có tồn tại không
        User user = this.repo.findById(request.id())
                .orElseThrow(() -> new UserNotFoundException(
                        String.format("Cannot update User: No User found with the provided ID: %s", request.id())));

        mergeUser(user, request);
        this.repo.save(user);

        if (StringUtils.isNotBlank(request.firstName()) || StringUtils.isNotBlank(request.lastName()) ||
                StringUtils.isNotBlank(request.email()) || StringUtils.isNotBlank(request.username())) {

            try {
                var keycloakUserResource = realmResource().users().get(user.getKeycloakId());
                var userRep = keycloakUserResource.toRepresentation();

                if (StringUtils.isNotBlank(request.firstName())) {
                    userRep.setFirstName(request.firstName());
                }
                if (StringUtils.isNotBlank(request.lastName())) {
                    userRep.setLastName(request.lastName());
                }
                if (StringUtils.isNotBlank(request.email())) {
                    userRep.setEmail(request.email());
                }
                if (StringUtils.isNotBlank(request.username())) {
                    userRep.setUsername(request.username());
                }

                keycloakUserResource.update(userRep);
            } catch (Exception ex) {
                logger.warn("⚠ Failed to update user in Keycloak for ID: {}", user.getKeycloakId(), ex);
            }
        }
    }

    private void mergeUser(User user, UserRequest request) {
        if (StringUtils.isNotBlank(request.phone())) {
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

    public Page<UserResponse> findAllUsers(int page, int size) {
        int first = page * size;

        // Lấy danh sách người dùng từ Keycloak
        List<UserRepresentation> kcUsers = realmResource().users().list(first, size);
        long total = realmResource().users().count();

        List<UserResponse> dtos = kcUsers.stream().map(u -> {
            // Lấy danh sách roles từ Keycloak user
            UserResource userResource = realmResource().users().get(u.getId());

            List<RoleRepresentation> realmRoles = userResource.roles().realmLevel().listAll();

            List<String> roleNames = realmRoles.stream()
                    .map(RoleRepresentation::getName)
                    .collect(Collectors.toList());

            String role = roleNames.stream()
                    .filter(r -> List.of("admin", "doctor", "patient").contains(r))
                    .findFirst()
                    .orElse(null);

            // Tạo fullName
            String fullName = String.format("%s %s",
                    u.getFirstName() != null ? u.getFirstName() : "",
                    u.getLastName() != null ? u.getLastName() : "").trim();
            if (fullName.isEmpty())
                fullName = null;

            User user = repo.findByKeycloakId(u.getId()).orElse(null);

            // Ánh xạ thành UserResponse
            UserResponse resp = mapper.coreToResponse(
                    user != null ? user.getId() : null,
                    u.getUsername(),
                    u.getEmail(),
                    fullName,
                    role
            );

            // Gộp dữ liệu profile từ MongoDB nếu có
            return repo.findByKeycloakId(u.getId())
                    .map(profile -> mapper.fillProfile(resp, profile))
                    .orElse(resp);

        }).toList();

        return new PageImpl<>(dtos, PageRequest.of(page, size), total);
    }

    public User findRawByKeycloakId(String keycloakId) {
        User user = this.repo.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new UserNotFoundException(
                        String.format("Cannot find User:: No User found with the provided Keycloak ID: %s",
                                keycloakId)));
        return user;
    }

    public User findRawByUserId(String userId) {
        User user = this.repo.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(
                        String.format("Cannot find User:: No User found with the provided ID: %s",
                                userId)));

        return user;
    }

    public UserResponse findById(String keycloakId) {
        logger.debug("⚡ Finding user by Keycloak ID: {}", keycloakId);

        UserRepresentation userRepresentation;
        try {
            userRepresentation = realmResource().users().get(keycloakId).toRepresentation();
        } catch (Exception ex) {
            logger.error("Failed to fetch user from Keycloak for ID: {}", keycloakId, ex);
            throw new UserNotFoundException(String.format("No User found in Keycloak with ID: %s", keycloakId));
        }

        if (userRepresentation == null) {
            logger.warn("⚠ No UserRepresentation found for ID: {}", keycloakId);
            throw new UserNotFoundException(String.format("No User found with ID: %s", keycloakId));
        }

        // Step 1: Lấy role hợp lệ duy nhất
        String role = null;
        try {
            List<String> roles = realmResource()
                    .users()
                    .get(keycloakId)
                    .roles()
                    .realmLevel()
                    .listAll()
                    .stream()
                    .map(r -> r.getName())
                    .toList();

            role = roles.stream()
                    .filter(r -> List.of("admin", "doctor", "patient").contains(r))
                    .findFirst()
                    .orElse(null);
        } catch (Exception e) {
            logger.warn("⚠ Failed to fetch roles for user ID: {}", keycloakId, e);
        }

        String fullName = String.format("%s %s",
                userRepresentation.getFirstName() != null ? userRepresentation.getFirstName() : "",
                userRepresentation.getLastName() != null ? userRepresentation.getLastName() : "").trim();
        if (fullName.isEmpty())
            fullName = null;

        System.out.println("Keycloak ID: " + keycloakId);
        User users = findRawByKeycloakId(keycloakId);

        UserResponse response = mapper.coreToResponse(
                users.getId(),
                userRepresentation.getUsername(),
                userRepresentation.getEmail(),
                fullName,
                role);

        return repo.findByKeycloakId(keycloakId)
                .map(profile -> {
                    logger.debug("✅ Found additional profile data in MongoDB for keycloakId: {}", keycloakId);
                    return mapper.fillProfile(response, profile);
                })
                .orElse(response);
    }

    public void deleteUser(String mongoId) {
        User user = repo.findById(mongoId)
                .orElseThrow(() -> new UserNotFoundException(
                        String.format("❌ Cannot delete: No user found in MongoDB with ID: %s", mongoId)));

        if (StringUtils.isNotBlank(user.getKeycloakId())) {
            try {
                realmResource().users().get(user.getKeycloakId()).remove();
                logger.info("🗑️ Successfully deleted user in Keycloak: {}", user.getKeycloakId());
            } catch (Exception e) {
                logger.error("⚠️ Failed to delete user in Keycloak: {}", user.getKeycloakId(), e);
            }
        }

        // 3. Xoá mềm trong MongoDB (nếu dùng soft-delete)
        user.setDeletedAt(LocalDateTime.now());
        repo.save(user);
        logger.info("✅ Soft-deleted user in MongoDB with ID: {}", mongoId);
    }

}