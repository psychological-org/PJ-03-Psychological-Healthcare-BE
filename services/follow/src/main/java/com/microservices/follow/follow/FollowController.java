package com.microservices.follow.follow;

import java.util.List;

import com.microservices.follow.utils.PagedResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<PagedResponse<FollowResponse>> findAll(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "limit", defaultValue = "10") int limit
    ) {
        return ResponseEntity.ok(this.service.findAllFollows(page, limit));
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

    @GetMapping("friends/{user-id}")
    public ResponseEntity<PagedResponse<FollowResponse>> findFriends(
            @PathVariable("user-id") String userId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "limit", defaultValue = "10") int limit){
        return ResponseEntity.ok(this.service.findAllFriendByUserId(userId, page, limit));
    }

    @GetMapping("/service/friends/{user-id}")
    public ResponseEntity<List<FollowResponse>> findAllFriendByUserIdNotPaginate(
            @PathVariable("user-id") String userId){
        return ResponseEntity.ok(this.service.findAllFriendRequestByUserIdNotPaginate(userId));
    }

    @GetMapping("requests/{user-id}")
    public ResponseEntity<PagedResponse<FollowResponse>> findFriendRequests(
            @PathVariable("user-id") String userId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "limit", defaultValue = "10") int limit){
        return ResponseEntity.ok(this.service.findAllFriendRequestByUserId(userId, page, limit));
    }
}
