package com.microservices.comment.like_comment;

import feign.Param;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface LikeCommentRepository extends JpaRepository<LikeComment, Integer> {
    Optional<LikeComment> findByCommentIdAndUserIdAndDeletedAtIsNull(Integer commentId, String userId);

    @Query("SELECT lc FROM LikeComment lc WHERE lc.commentId = :commentId AND lc.deletedAt IS NULL")
    Page<LikeComment> findAllByCommentId(Integer commentId, Pageable pageable);

    @Query("SELECT lc FROM LikeComment lc WHERE lc.userId = :userId AND lc.deletedAt IS NULL")
    Page<LikeComment> findAllByUserId(String userId, Pageable pageable);

    @Query("SELECT lc FROM LikeComment lc WHERE lc.id = :id AND lc.deletedAt IS NULL")
    LikeComment findOneById(Integer id);

    @Query("SELECT lc FROM LikeComment lc WHERE lc.deletedAt IS NULL")
    Page<LikeComment> findAllLikeComment(Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE LikeComment lc SET lc.deletedAt = CURRENT_TIMESTAMP WHERE lc.id = :id")
    void softDeleteById(Integer id);

    @Query("SELECT COUNT(*) > 0 FROM LikeComment lc WHERE lc.commentId = :commentId AND lc.userId = :userId AND lc.deletedAt IS NULL")
    boolean existsByCommentIdAndUserId(Integer commentId, String userId);
}
