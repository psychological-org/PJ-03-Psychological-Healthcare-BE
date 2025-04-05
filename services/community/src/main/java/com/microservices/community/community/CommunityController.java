package com.microservices.community.community;

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
@RequestMapping("/api/v1/communities")
@RequiredArgsConstructor
public class CommunityController {
    private final CommunityService service;

    @PostMapping
    public ResponseEntity<Integer> createCommunity(
            @RequestBody @Valid CommunityRequest request) {
        return ResponseEntity.ok(this.service.createCommunity(request));
    }

    @PutMapping
    public ResponseEntity<Void> updateCommunity(
            @RequestBody @Valid CommunityRequest request) {
        this.service.updateCommunity(request);
        return ResponseEntity.accepted().build();
    }

    @GetMapping
    public ResponseEntity<List<CommunityResponse>> findAll() {
        return ResponseEntity.ok(this.service.findAllCommunitys());
    }

    @GetMapping("/exists/{community-id}")
    public ResponseEntity<Boolean> existsById(
            @PathVariable("community-id") Integer userId) {
        return ResponseEntity.ok(this.service.existsById(userId));
    }

    @GetMapping("/{community-id}")
    public ResponseEntity<CommunityResponse> findById(
            @PathVariable("community-id") Integer userId) {
        return ResponseEntity.ok(this.service.findById(userId));
    }

    @DeleteMapping("/{community-id}")
    public ResponseEntity<Void> delete(
            @PathVariable("community-id") Integer userId) {
        this.service.deleteCommunity(userId);
        return ResponseEntity.accepted().build();
    }
}
