package com.microservices.post.like_post;

import com.microservices.post.exception.PostNotFoundException;
import com.microservices.post.post.Post;
import com.microservices.post.post.PostRepository;
import com.microservices.post.post.PostResponse;
import com.microservices.post.utils.PagedResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LikePostService {
    private final LikePostRepository likePostRepository;
    private final LikePostMapper likePostMapper;
    private final PostRepository postRepository;

    public Integer createLikePost(LikePostRequest likePostRequest) {
        Post post = postRepository.findById(likePostRequest.postId())
                .orElseThrow(() -> new RuntimeException("Post not found: " + likePostRequest.postId()));

        // Kiểm tra người dùng chưa like
        if (likePostRepository.existsByPostIdAndUserId(likePostRequest.postId(), likePostRequest.userId())) {
            throw new RuntimeException("User " + likePostRequest.userId() + " already liked post " + likePostRequest.postId());
        }

        // Tạo LikePost
        LikePost likePost = LikePost.builder()
                .postId(likePostRequest.postId())
                .userId(likePostRequest.userId())
                .build();
        likePost = likePostRepository.save(likePost);

        // Tăng reactCount
        post.setReactCount(post.getReactCount() + 1);
        postRepository.save(post);

        return likePost.getId();
    }

    public PagedResponse<LikePostResponse> getLikePostById(Integer id, Integer page, Integer limit) {
        Pageable pageable = PageRequest.of(page, limit); // You can adjust the page and limit as needed
        Page<LikePost> likePosts = likePostRepository.findAllByPostId(id, pageable);

        if (likePosts.getContent().isEmpty()) {
            throw new PostNotFoundException("No likes found for post with ID: " + id);
        }

        List<LikePostResponse> likePostResponses = likePosts.getContent()
                .stream()
                .map(likePostMapper::fromLikePost)
                .collect(Collectors.toList());

        return new PagedResponse<>(likePostResponses, likePosts.getTotalPages(), likePosts.getTotalElements());
    }

    public PagedResponse<LikePostResponse> getLikePostByUserId(String userId, Integer page, Integer limit) {
        Pageable pageable = PageRequest.of(page, limit); // You can adjust the page and limit as needed
        Page<LikePost> likePosts = likePostRepository.findAllByUserId(userId, pageable);

        if (likePosts.getContent().isEmpty()) {
            throw new PostNotFoundException("No likes found for user with ID: " + userId);
        }

        List<LikePostResponse> likePostResponses = likePosts.getContent()
                .stream()
                .map(likePostMapper::fromLikePost)
                .collect(Collectors.toList());

        return new PagedResponse<>(likePostResponses, likePosts.getTotalPages(), likePosts.getTotalElements());
    }

    public void deleteLikePost(Integer id) {
        LikePost likePost = likePostRepository.findOneById(id);
        if (likePost == null) {
            throw new RuntimeException("LikePost not found or already deleted: " + id);
        }

        // Giảm reactCount
        Post post = postRepository.findById(likePost.getPostId())
                .orElseThrow(() -> new RuntimeException("Post not found: " + likePost.getPostId()));
        post.setReactCount(Math.max(0, post.getReactCount() - 1));
        postRepository.save(post);

        // Xóa mềm LikePost
        likePostRepository.softDeleteById(id);
    }

    public boolean isPostLiked(Integer postId, String userId) {
        return likePostRepository.findByPostIdAndUserIdAndDeletedAtIsNull(postId, userId).isPresent();
    }

    private void updatePostReactCount(Integer postId, int delta) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found with ID: " + postId));
        post.setReactCount(post.getReactCount() + delta);
        postRepository.save(post);
    }

}

