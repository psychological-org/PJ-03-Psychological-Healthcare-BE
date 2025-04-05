package com.microservices.post.post;

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
    public ResponseEntity<List<PostResponse>> findAll() {
        return ResponseEntity.ok(this.service.findAllPosts());
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
