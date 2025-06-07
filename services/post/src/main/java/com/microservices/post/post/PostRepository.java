package com.microservices.post.post;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Integer> {

    Page<Post> findByUserIdAndDeletedAtIsNull(String userId, Pageable pageable);
    Page<Post> findByCommunityIdAndDeletedAtIsNull(Integer communityId, Pageable pageable);

    @Query("SELECT u FROM Post u WHERE u.id = :id AND u.deletedAt IS NULL")
    Optional<Post> findOneById(Integer id);

    @Query("SELECT u FROM Post u WHERE u.deletedAt IS NULL")
    public Page<Post> findAllUsers(Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE Post u SET u.deletedAt = CURRENT_TIMESTAMP WHERE u.id = :id")
    void softDeleteById(Integer id);
}
