package com.microservices.follow.follow;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/follows")
@RequiredArgsConstructor
public class FollowController {
    private final FollowService service;

    @PostMapping
    public ResponseEntity<Integer> createFollow(
            @RequestBody @Valid FollowRequest request) {
        return ResponseEntity.ok(this.service.createFollow(request));
    }

    @PutMapping
    public ResponseEntity<Void> updateFollow(
            @RequestBody @Valid FollowRequest request) {
        this.service.updateFollow(request);
        return ResponseEntity.accepted().build();
    }

    @GetMapping
    public ResponseEntity<List<FollowResponse>> findAll() {
        return ResponseEntity.ok(this.service.findAllFollows());
    }

    @GetMapping("/exists/{follow-id}")
    public ResponseEntity<Boolean> existsById(
            @PathVariable("follow-id") Integer userId) {
        return ResponseEntity.ok(this.service.existsById(userId));
    }

    @GetMapping("/{follow-id}")
    public ResponseEntity<FollowResponse> findById(
            @PathVariable("follow-id") Integer userId) {
        return ResponseEntity.ok(this.service.findById(userId));
    }

    @DeleteMapping("/{follow-id}")
    public ResponseEntity<Void> delete(
            @PathVariable("follow-id") Integer userId) {
        this.service.deleteFollow(userId);
        return ResponseEntity.accepted().build();
    }
}
