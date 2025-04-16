package com.microservices.notification.user_notification;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface UserNotificationRepository extends MongoRepository<UserNotification, String> {
    // Find all user notifications by user id
    @Query("{ 'deletedAt': null, 'userId': ?0 }")
    UserNotification findByUserId(String userId);

    // Find all user notifications by user id and notification id
    @Query("{ 'deletedAt': null, 'userId': ?0, 'notificationId': ?1 }")
    UserNotification findByUserIdAndNotificationId(String userId, String notificationId);

    @Query("{ 'deletedAt': null, 'id': ?0 }")
    UserNotification findOneById(String id);

}
