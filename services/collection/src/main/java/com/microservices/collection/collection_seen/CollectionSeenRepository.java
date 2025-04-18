package com.microservices.collection.collection_seen;

import com.microservices.collection.utils.PagedResponse;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

@Service
public interface CollectionSeenRepository extends JpaRepository<CollectionSeen, Integer> {
    // Get collection seen by collection id
    @Query("SELECT u FROM CollectionSeen u WHERE u.collectionId = :collectionId AND u.deletedAt IS NULL")
    CollectionSeen findByCollectionId(Integer collectionId);

    // Get collection seen by user id
    @Query("SELECT u FROM CollectionSeen u WHERE u.userId = :userId AND u.deletedAt IS NULL")
    public Page<CollectionSeen> findByUserId(String userId, Pageable pageable);

    // Get collection seen by collection_seen id
    @Query("SELECT u FROM CollectionSeen u WHERE u.id = :id AND u.deletedAt IS NULL")
    Optional<CollectionSeen> findOneById(Integer id);

    @Query("SELECT u FROM CollectionSeen u WHERE u.deletedAt IS NULL")
    public Page<CollectionSeen> findAllCollectionSeen(Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE CollectionSeen u SET u.deletedAt = CURRENT_TIMESTAMP WHERE u.id = :id")
    void softDeleteById(Integer id);
}
