package com.microservices.follow.follow;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Integer> {
    @Query("SELECT u FROM Follow u WHERE u.id = :id AND u.deletedAt IS NULL")
    Optional<Follow> findOneById(Integer id);

    @Query("SELECT u FROM Follow u WHERE u.deletedAt IS NULL")
    public Page<Follow> findAllFollow(Pageable pageable);

    @Query("""
        SELECT u FROM Follow u
        WHERE (u.senderId = :userId OR u.receiverId = :userId)
          AND u.status = 'accepted'
          AND u.deletedAt IS NULL
    """)
    public Page<Follow> findAllFollowByUserId(String userId, Pageable pageable);

    @Query("""
        SELECT u FROM Follow u
        WHERE (u.senderId = :userId OR u.receiverId = :userId)
          AND u.status = 'accepted'
          AND u.deletedAt IS NULL
    """)
    public List<Follow> findAllFollowByUserIdNotPaginate(String userId);

    @Query("""
        SELECT u FROM Follow u
        WHERE (u.receiverId = :userId)
          AND u.status = 'pending'
          AND u.deletedAt IS NULL
    """)
    public Page<Follow> findAllRequestByUserId(String userId, Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE Follow u SET u.deletedAt = CURRENT_TIMESTAMP WHERE u.id = :id")
    void softDeleteById(Integer id);



}
