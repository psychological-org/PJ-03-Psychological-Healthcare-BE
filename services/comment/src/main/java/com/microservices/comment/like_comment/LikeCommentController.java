package com.microservices.comment.like_comment;


import com.microservices.comment.utils.PagedResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/comments/like-comment")
@RequiredArgsConstructor
public class LikeCommentController {
    private final LikeCommentService service;

    // [SỬA] Đổi tên thành createLikeComment
    @PostMapping
    public ResponseEntity<Integer> createLikeComment(
            @RequestBody @Valid LikeCommentRequest request
    ) {
        return ResponseEntity.ok(this.service.createLikeComment(request));
    }

    // [SỬA] Dùng comment-id thay vì post-id
    @GetMapping("/comment/{comment-id}")
    public ResponseEntity<PagedResponse<LikeCommentResponse>> getLikeCommentByCommentId(
            @PathVariable("comment-id") Integer commentId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "limit", defaultValue = "10") int limit
    ) {
        return ResponseEntity.ok(this.service.getLikeCommentByCommentId(commentId, page, limit));
    }

    // [SỬA] Giữ nguyên user-id
    @GetMapping("/user/{user-id}")
    public ResponseEntity<PagedResponse<LikeCommentResponse>> getLikeCommentByUserId(
            @PathVariable("user-id") String userId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "limit", defaultValue = "10") int limit
    ) {
        return ResponseEntity.ok(this.service.getLikeCommentByUserId(userId, page, limit));
    }

    // [SỬA] Đổi tên thành deleteLikeComment
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLikeComment(
            @PathVariable("id") Integer id
    ) {
        this.service.deleteLikeComment(id);
        return ResponseEntity.accepted().build();
    }

    // [SỬA] Dùng commentId và isCommentLiked
    @GetMapping("/{commentId}/user/{userId}")
    public ResponseEntity<Boolean> isCommentLiked(
            @PathVariable("commentId") Integer commentId,
            @PathVariable("userId") String userId) {
        return ResponseEntity.ok(this.service.isCommentLiked(commentId, userId));
    }
}
