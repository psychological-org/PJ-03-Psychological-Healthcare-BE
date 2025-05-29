package com.microservices.community.community;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CommunityRepository extends JpaRepository<Community, Integer> {
    @Query("SELECT u FROM Community u WHERE u.id = :id AND u.deletedAt IS NULL")
    Optional<Community> findOneById(Integer id);

    @Query("SELECT u FROM Community u WHERE u.deletedAt IS NULL")
    public Page<Community> findAllCommunities(Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE Community u SET u.deletedAt = CURRENT_TIMESTAMP WHERE u.id = :id")
    void softDeleteById(Integer id);
}
