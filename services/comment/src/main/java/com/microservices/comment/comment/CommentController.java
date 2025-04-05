package com.microservices.comment.comment;

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
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService service;

    @PostMapping
    public ResponseEntity<Integer> createComment(
            @RequestBody @Valid CommentRequest request) {
        return ResponseEntity.ok(this.service.createComment(request));
    }

    @PutMapping
    public ResponseEntity<Void> updateComment(
            @RequestBody @Valid CommentRequest request) {
        this.service.updateComment(request);
        return ResponseEntity.accepted().build();
    }

    @GetMapping
    public ResponseEntity<List<CommentResponse>> findAll() {
        return ResponseEntity.ok(this.service.findAllComments());
    }

    @GetMapping("/exists/{comment-id}")
    public ResponseEntity<Boolean> existsById(
            @PathVariable("comment-id") Integer userId) {
        return ResponseEntity.ok(this.service.existsById(userId));
    }

    @GetMapping("/{comment-id}")
    public ResponseEntity<CommentResponse> findById(
            @PathVariable("comment-id") Integer userId) {
        return ResponseEntity.ok(this.service.findById(userId));
    }

    @DeleteMapping("/{comment-id}")
    public ResponseEntity<Void> delete(
            @PathVariable("comment-id") Integer userId) {
        this.service.deleteComment(userId);
        return ResponseEntity.accepted().build();
    }
}
