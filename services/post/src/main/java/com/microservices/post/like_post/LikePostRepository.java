package com.microservices.post.like_post;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface LikePostRepository extends JpaRepository<LikePost, Integer> {
    @Query("SELECT lp FROM LikePost lp WHERE lp.postId = :postId AND lp.deletedAt IS NULL")
    Page<LikePost> findAllByPostId(Integer postId, Pageable pageable);

    @Query("SELECT lp FROM LikePost lp WHERE lp.userId = :userId AND lp.deletedAt IS NULL")
    Page<LikePost> findAllByUserId(String userId, Pageable pageable);

    @Query("SELECT lp FROM LikePost lp WHERE lp.id = :id AND lp.deletedAt IS NULL")
    LikePost findOneById(Integer id);

    @Query("SELECT lp FROM LikePost lp WHERE lp.deletedAt IS NULL")
    Page<LikePost> findAllLikePost(Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE LikePost lp SET lp.deletedAt = CURRENT_TIMESTAMP WHERE lp.id = :id")
    void softDeleteById(Integer id);
}
