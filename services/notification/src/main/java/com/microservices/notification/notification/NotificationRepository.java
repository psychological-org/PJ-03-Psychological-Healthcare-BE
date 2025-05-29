package com.microservices.notification.notification;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface NotificationRepository extends MongoRepository<Notification, String> {
    @Query("{ 'deletedAt': null }")
    Page<Notification> findAllNotifications(Pageable pageable);

    // Get by id where deletedAt = null
    @Query("{ 'deletedAt': null, 'id': ?0 }")
    Optional<Notification> findOneById(String id);

    // Delete document by id
    Optional<Notification> findByIdAndDeletedAtIsNull(String id);

    @Query("{ 'deletedAt': null, 'name': ?0 }")
    Notification findByNameAndDeletedAtIsNull(String name);

}

