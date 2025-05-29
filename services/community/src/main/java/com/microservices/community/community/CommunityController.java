package com.microservices.community.community;

import com.microservices.community.utils.PagedResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<PagedResponse<CommunityResponse>> findAll(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "limit", defaultValue = "10") int limit
    ) {
        return ResponseEntity.ok(this.service.findAllCommunities(page, limit));
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
