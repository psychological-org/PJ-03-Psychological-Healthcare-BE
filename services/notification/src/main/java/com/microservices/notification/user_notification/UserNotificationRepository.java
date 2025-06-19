package com.microservices.notification.user_notification;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface UserNotificationRepository extends MongoRepository<UserNotification, String> {
    // Find all user notifications by user id
    @Query("{ 'deletedAt': null, 'userId': ?0 }")
    Page<UserNotification> findByUserId(String userId, Pageable pageable);

    // Find all user notifications by user id and notification id
    @Query("{ 'deletedAt': null, 'userId': ?0, 'notificationId': ?1 }")
    UserNotification findByUserIdAndNotificationId(String userId, String notificationId);

    @Query("{ 'deletedAt': null, 'id': ?0 }")
    UserNotification findOneById(String id);

    // Tìm tất cả các thông báo đã xóa
    @Query("{ 'deletedAt': { $ne: null } }")
    Page<UserNotification> findAllDeletedNotifications(Pageable pageable);

}
