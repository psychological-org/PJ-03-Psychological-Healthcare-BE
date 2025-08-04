package com.microservices.topic.topic;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface TopicRepository extends JpaRepository<Topic, Integer> {
    @Query("SELECT u FROM Topic u WHERE u.id = :id AND u.deletedAt IS NULL")
    Optional<Topic> findOneById(Integer id);

    @Query("SELECT u FROM Topic u WHERE u.deletedAt IS NULL")
    public Page<Topic> findAllTopics(Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE Topic u SET u.deletedAt = CURRENT_TIMESTAMP WHERE u.id = :id")
    void softDeleteById(Integer id);
}
