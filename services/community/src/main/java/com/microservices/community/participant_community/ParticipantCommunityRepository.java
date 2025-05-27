package com.microservices.community.participant_community;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface ParticipantCommunityRepository extends JpaRepository<ParticipantCommunity, Integer> {
    @Query("SELECT u FROM ParticipantCommunity u WHERE u.communityId = :communityId AND u.deletedAt IS NULL")
    Page<ParticipantCommunity> findByCommunityId(Integer communityId, Pageable pageable);

    // Get collection seen by user id
    @Query("SELECT u FROM ParticipantCommunity u WHERE u.userId = :userId AND u.deletedAt IS NULL")
    Page<ParticipantCommunity> findByUserId(String userId, Pageable pageable);

    // Get collection seen by participant_community id
    @Query("SELECT u FROM ParticipantCommunity u WHERE u.id = :id AND u.deletedAt IS NULL")
    Optional<ParticipantCommunity> findOneById(Integer id);

    @Query("SELECT u FROM ParticipantCommunity u WHERE u.deletedAt IS NULL")
    public Page<ParticipantCommunity> findAllCollectionSeen(Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE ParticipantCommunity u SET u.deletedAt = CURRENT_TIMESTAMP WHERE u.id = :id")
    void softDeleteById(Integer id);
}
