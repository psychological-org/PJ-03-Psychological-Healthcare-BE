package com.microservices.user.user;

import com.microservices.user.utils.PagedResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService service;

    @PostMapping
    public ResponseEntity<String> createUser(
            @RequestBody @Valid UserRequest request) {
        return ResponseEntity.ok(this.service.createUser(request));
    }

    @PutMapping
    public ResponseEntity<Void> updateUser(
            @RequestBody @Valid UserRequest request) {
        this.service.updateUser(request);
        return ResponseEntity.accepted().build();
    }

    @GetMapping
    public ResponseEntity<PagedResponse<UserResponse>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<UserResponse> p = service.findAllUsers(page, size);
        return ResponseEntity.ok(
                new PagedResponse<>(p.getContent(), p.getTotalPages(), p.getTotalElements()));
    }

    @GetMapping("/{user-id}")
    public ResponseEntity<UserResponse> findById(
            @PathVariable("user-id") String userId) {
        User user = this.service.findRawByKeycloakId(userId);
        return ResponseEntity.ok(this.service.findById(user.getKeycloakId()));
    }

    @GetMapping("profile")
    public ResponseEntity<UserResponse> getProfile(@AuthenticationPrincipal Jwt jwt) {
        String keycloakId = jwt.getClaim("sub");
        return ResponseEntity.ok(this.service.findById(keycloakId));
    }

    @DeleteMapping("/{user-id}")
    public ResponseEntity<Void> delete(
            @PathVariable("user-id") String userId) {
        this.service.deleteUser(userId);
        return ResponseEntity.accepted().build();
    }
}