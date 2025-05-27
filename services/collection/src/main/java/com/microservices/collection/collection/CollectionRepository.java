package com.microservices.collection.collection;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CollectionRepository extends JpaRepository<Collection, Integer> {

    @Query("SELECT u FROM Collection u WHERE u.id = :id AND u.deletedAt IS NULL")
    Optional<Collection> findOneById(Integer id);

    @Query("SELECT u FROM Collection u WHERE u.deletedAt IS NULL")
    public Page<Collection> findAllCollections(Pageable pageable);

    @Query("SELECT u FROM Collection u WHERE u.topicId = :topicId AND u.deletedAt IS NULL")
    public List<Collection> findAllCollectionsByTopicId(Integer topicId);

    @Modifying
    @Transactional
    @Query("UPDATE Collection u SET u.deletedAt = CURRENT_TIMESTAMP WHERE u.id = :id")
    void softDeleteById(Integer id);
}
