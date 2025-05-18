package com.microservices.comment.comment;

import com.microservices.comment.utils.PagedResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public
    ResponseEntity<PagedResponse<CommentResponse>> findAll(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "limit", defaultValue = "10") int limit
    ) {
        return ResponseEntity.ok(this.service.findAllComments(page, limit));
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

    @GetMapping("/post/{post-id}")
    public ResponseEntity<PagedResponse<CommentResponse>> findByPostId(
            @PathVariable("post-id") Integer postId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "limit", defaultValue = "10") int limit
    ) {
        return ResponseEntity.ok(this.service.findAllCommentsByPostId(postId, page, limit));
    }
}
