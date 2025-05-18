package com.microservices.post.post;

import com.microservices.post.utils.PagedResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService service;

    @PostMapping
    public ResponseEntity<Integer> createPost(
            @RequestBody @Valid PostRequest request) {
        return ResponseEntity.ok(this.service.createPost(request));
    }

    @PutMapping
    public ResponseEntity<Void> updatePost(
            @RequestBody @Valid PostRequest request) {
        this.service.updatePost(request);
        return ResponseEntity.accepted().build();
    }

    @GetMapping
    public ResponseEntity<PagedResponse<PostResponse>> findAll(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "limit", defaultValue = "10") int limit
    ) {
        return ResponseEntity.ok(this.service.findAllPosts(page, limit));
    }

    @GetMapping("/exists/{post-id}")
    public ResponseEntity<Boolean> existsById(
            @PathVariable("post-id") Integer userId) {
        return ResponseEntity.ok(this.service.existsById(userId));
    }

    @GetMapping("/{post-id}")
    public ResponseEntity<PostResponse> findById(
            @PathVariable("post-id") Integer userId) {
        return ResponseEntity.ok(this.service.findById(userId));
    }

    @DeleteMapping("/{post-id}")
    public ResponseEntity<Void> delete(
            @PathVariable("post-id") Integer userId) {
        this.service.deletePost(userId);
        return ResponseEntity.accepted().build();
    }
}
