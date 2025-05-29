package com.microservices.post.like_post;

import com.microservices.post.utils.PagedResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/posts/like-post")
@RequiredArgsConstructor
public class LikePostController {
    private final LikePostService service;

    @PostMapping()
    public ResponseEntity<Integer> createLikePost(
            @RequestBody @Valid LikePostRequest request
    ) {
        return ResponseEntity.ok(this.service.createLikePost(request));
    }

    @GetMapping("/postId/{post-id}")
    public ResponseEntity<PagedResponse<LikePostResponse>> getLikePostByPostId(
            @PathVariable("post-id") Integer postId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "limit", defaultValue = "10") int limit
    ) {
        return ResponseEntity.ok(this.service.getLikePostById(postId, page, limit));
    }

    @GetMapping("/userId/{user-id}")
    public ResponseEntity<PagedResponse<LikePostResponse>> getLikePostByUserId(
            @PathVariable("user-id") String userId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "limit", defaultValue = "10") int limit
    ) {
        return ResponseEntity.ok(this.service.getLikePostByUserId(userId, page, limit));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLikePost(
            @PathVariable("id") Integer id
    ) {
        this.service.deleteLikePost(id);
        return ResponseEntity.accepted().build();
    }
}
