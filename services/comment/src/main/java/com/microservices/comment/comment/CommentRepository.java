package com.microservices.comment.comment;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    @Query("SELECT u FROM Comment u WHERE u.id = :id AND u.deletedAt IS NULL")
    Optional<Comment> findOneById(Integer id);

    @Query("SELECT u FROM Comment u WHERE u.deletedAt IS NULL")
    public Page<Comment> findAllComment(Pageable pageable);

    @Query("SELECT u FROM Comment u WHERE u.postId = :postId AND u.deletedAt IS NULL")
    public Page<Comment> findAllCommentByPostId(Integer postId, Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE Comment u SET u.deletedAt = CURRENT_TIMESTAMP WHERE u.id = :id")
    void softDeleteById(Integer id);
}
