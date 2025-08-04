package com.microservices.comment.like_comment;

import com.microservices.comment.comment.Comment;
import com.microservices.comment.comment.CommentRepository;
import com.microservices.comment.exception.CommentNotFoundException;
import com.microservices.comment.utils.PagedResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LikeCommentService {
    private final LikeCommentRepository likeCommentRepository;
    private final LikeCommentMapper likeCommentMapper;
    private final CommentRepository commentRepository;

    public Integer createLikeComment(LikeCommentRequest likeCommentRequest) {
        // [SỬA] Kiểm tra bình luận tồn tại
        Comment comment = commentRepository.findOneById(likeCommentRequest.commentId())
                .orElseThrow(() -> new CommentNotFoundException("Comment not found: " + likeCommentRequest.commentId()));

        // [SỬA] Kiểm tra người dùng chưa like
        if (likeCommentRepository.existsByCommentIdAndUserId(likeCommentRequest.commentId(), likeCommentRequest.userId())) {
            throw new RuntimeException("User " + likeCommentRequest.userId() + " already liked comment " + likeCommentRequest.commentId());
        }

        // [SỬA] Tạo LikeComment
        LikeComment likeComment = likeCommentMapper.toLikeComment(likeCommentRequest);
        likeComment = likeCommentRepository.save(likeComment);

        // [SỬA] Tăng reactCount
        comment.setReactCount((comment.getReactCount() != null ? comment.getReactCount() : 0) + 1);
        commentRepository.save(comment);

        return likeComment.getId();
    }

    public PagedResponse<LikeCommentResponse> getLikeCommentByCommentId(Integer commentId, int page, int limit) {
        // [SỬA] Lấy danh sách like theo commentId
        Pageable pageable = PageRequest.of(page, limit);
        Page<LikeComment> likeComments = likeCommentRepository.findAllByCommentId(commentId, pageable);

        if (likeComments.getContent().isEmpty()) {
            throw new CommentNotFoundException("No likes found for comment with ID: " + commentId);
        }

        List<LikeCommentResponse> likeCommentResponses = likeComments.getContent()
                .stream()
                .map(likeCommentMapper::fromLikeComment)
                .collect(Collectors.toList());

        return new PagedResponse<>(likeCommentResponses, likeComments.getTotalPages(), likeComments.getTotalElements());
    }

    public PagedResponse<LikeCommentResponse> getLikeCommentByUserId(String userId, int page, int limit) {
        // [SỬA] Lấy danh sách like theo userId
        Pageable pageable = PageRequest.of(page, limit);
        Page<LikeComment> likeComments = likeCommentRepository.findAllByUserId(userId, pageable);

        if (likeComments.getContent().isEmpty()) {
            throw new CommentNotFoundException("No likes found for user with ID: " + userId);
        }

        List<LikeCommentResponse> likeCommentResponses = likeComments.getContent()
                .stream()
                .map(likeCommentMapper::fromLikeComment)
                .collect(Collectors.toList());

        return new PagedResponse<>(likeCommentResponses, likeComments.getTotalPages(), likeComments.getTotalElements());
    }

    public void deleteLikeComment(Integer id) {
        // [SỬA] Tìm LikeComment
        LikeComment likeComment = likeCommentRepository.findOneById(id);
        if (likeComment == null) {
            throw new RuntimeException("LikeComment not found or already deleted: " + id);
        }

        // [SỬA] Giảm reactCount
        Comment comment = commentRepository.findOneById(likeComment.getCommentId())
                .orElseThrow(() -> new CommentNotFoundException("Comment not found: " + likeComment.getCommentId()));
        comment.setReactCount(Math.max(0, (comment.getReactCount() != null ? comment.getReactCount() : 0) - 1));
        commentRepository.save(comment);

        // [SỬA] Xóa mềm LikeComment
        likeCommentRepository.softDeleteById(id);
    }

    public boolean isCommentLiked(Integer commentId, String userId) {
        // [SỬA] Kiểm tra trạng thái like
        return likeCommentRepository.existsByCommentIdAndUserId(commentId, userId);
    }
}
