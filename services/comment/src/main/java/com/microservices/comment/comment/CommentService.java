package com.microservices.comment.comment;

import java.util.List;
import java.util.stream.Collectors;

import com.microservices.comment.exception.PostNotFoundException;
import com.microservices.comment.exception.UserNotFoundException;
import com.microservices.comment.kafka.CommentProducer;
import com.microservices.comment.like_comment.LikeComment;
import com.microservices.comment.like_comment.LikeCommentRepository;
import com.microservices.comment.like_comment.LikeCommentRequest;
import com.microservices.comment.like_comment.LikeCommentService;
import com.microservices.comment.post.PostClient;
import com.microservices.comment.post.PostResponse;
import com.microservices.comment.user.UserClient;
import com.microservices.comment.user.UserResponse;
import com.microservices.comment.utils.PagedResponse;
import feign.FeignException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.microservices.comment.exception.CommentNotFoundException;

import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository repository;
    private final CommentMapper mapper;
    private final UserClient userClient;
    private final PostClient postClient;
    private final CommentProducer commentProducer;
    private final LikeCommentService likeCommentService;
    private final LikeCommentRepository likeCommentRepository;

    public Integer createComment(CommentRequest request) {
        try {
            ResponseEntity<UserResponse> response = userClient.findById(request.userId());
            if (response == null || response.getBody() == null) {
                throw new UserNotFoundException(
                        String.format("Cannot create community:: No user found with ID: %s", request.userId()));
            }

            // Check if the post exists
            ResponseEntity<PostResponse> post = postClient.findById(request.postId());
            if (post  == null || post.getBody() == null) {
                throw new PostNotFoundException(
                        String.format("Cannot create community:: No post found with ID: %s", request.userId()));
            }

            var comment = this.repository.save(mapper.toComment(request));
            CommentResponse commentMapper = new CommentResponse(
                    comment.getId(),
                    comment.getContent(),
                    comment.getImageUrl(),
                    response.getBody().fullName(),
                    comment.getPostId(),
                    comment.getReactCount(),
                    comment.getCreatedAt()
            );
            // Send follow event to Kafka
            commentProducer.sendNotificationOfNewCommentToPost(mapper.fromComment(comment));
            return comment.getId();
        } catch (FeignException e) {
            throw new UserNotFoundException("User or post service not available or returned error: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error when creating community", e);
        }
    }

    public void updateComment(CommentRequest request) {
        var comment = this.repository.findById(request.id())
                .orElseThrow(() -> new CommentNotFoundException(
                        String.format("Cannot update comment:: No comment found with the provided ID: %s",
                                request.id())));
        mergeComment(comment, request);
        this.repository.save(comment);
    }

    private void mergeComment(Comment comment, CommentRequest request) {
        if (StringUtils.isNotBlank(request.content())) {
            comment.setContent(request.content());
        }
        if (StringUtils.isNotBlank(request.content())) {
            comment.setContent(request.content());
        }
        if (request.imageUrl() != null) {
            comment.setImageUrl(request.imageUrl());
        }
        if (request.reactCount() != null) {
            comment.setReactCount(request.reactCount());
        }
        if (request.userId() != null) {
            try {
                ResponseEntity<UserResponse> response = userClient.findById(request.userId());
                if (response == null || response.getBody() == null) {
                    throw new UserNotFoundException(
                            String.format("Cannot update comment:: No user found with ID: %s", request.userId()));
                }
                ResponseEntity<PostResponse> post = postClient.findById(request.postId());
                if (post  == null || post.getBody() == null) {
                    throw new PostNotFoundException(
                            String.format("Cannot create community:: No post found with ID: %s", request.userId()));
                }
                comment.setUserId(request.userId());
            } catch (FeignException e) {
                throw new UserNotFoundException("User or post service not available or returned error: " + e.getMessage());
            }
        }
    }

    public PagedResponse<CommentResponse> findAllComments(int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit);
        Page<Comment> appointments = this.repository.findAll(pageable);
        if (appointments.getContent().isEmpty()) {
            throw new CommentNotFoundException("No comment found");
        }
        List<CommentResponse> appointmentResponses = appointments.getContent()
                .stream()
                .map(this.mapper::fromComment)
                .collect(Collectors.toList());
        return new PagedResponse<>(appointmentResponses, appointments.getTotalPages(), appointments.getTotalElements());
    }

    public CommentResponse findById(Integer id) {
        return this.repository.findOneById(id)
                .map(mapper::fromComment)
                .orElseThrow(() -> new CommentNotFoundException(
                        String.format("No Comment found with the provided ID: %s", id)));
    }

    public boolean existsById(Integer id) {
        return this.repository.findOneById(id)
                .isPresent();
    }

    public void deleteComment(Integer id) {
        this.repository.softDeleteById(id);
    }

    public PagedResponse<CommentResponse> findAllCommentsByPostId(Integer postId, int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit);
        Page<Comment> comments = this.repository.findAllCommentByPostId(postId, pageable);
        if (comments.getContent().isEmpty()) {
            throw new CommentNotFoundException("No comment found");
        }
        List<CommentResponse> commentResponses = comments.getContent()
                .stream()
                .map(this.mapper::fromComment)
                .collect(Collectors.toList());
        return new PagedResponse<>(commentResponses, comments.getTotalPages(), comments.getTotalElements());
    }

    public void toggleLikeComment(Integer commentId, String userId) {
        boolean isLiked = likeCommentService.isCommentLiked(commentId, userId);
        if (isLiked) {
            // Tìm và xóa LikeComment
            LikeComment likeComment = likeCommentRepository.findByCommentIdAndUserIdAndDeletedAtIsNull(commentId, userId)
                    .orElseThrow(() -> new RuntimeException("LikeComment not found for comment " + commentId + " and user " + userId));
            likeCommentService.deleteLikeComment(likeComment.getId());
        } else {
            // Tạo LikeComment
            LikeCommentRequest request = new LikeCommentRequest(null, commentId, userId);
            likeCommentService.createLikeComment(request);
        }
    }

    // [THÊM MỚI] Kiểm tra trạng thái like
    public boolean isCommentLiked(Integer commentId, String userId) {
        return likeCommentService.isCommentLiked(commentId, userId);
    }
}