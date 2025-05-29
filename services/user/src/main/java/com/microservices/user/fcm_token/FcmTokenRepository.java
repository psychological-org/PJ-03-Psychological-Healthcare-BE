package com.microservices.user.fcm_token;

import com.microservices.user.user.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface FcmTokenRepository extends MongoRepository<FcmToken, String> {
    // Tìm FCM token theo userId
    @Query("{ 'deletedAt': null, 'userId': ?0 }")
    List<FcmToken> findByUserId(String userId);

    // Tìm FCM token theo id
    @Query("{ 'deletedAt': null, 'id': ?0 }")
    FcmToken findByIdAndDeletedAtIsNull(String id);

    // Tìm FCM token theo userId, deviceId và FCM token
    @Query("{ 'deletedAt': null, 'userId': ?0, 'deviceId': ?1, 'fcmToken': ?2 }")
    FcmToken findByUserIdAndDeviceIdAndFcmToken(String userId, String deviceId, String fcmToken);

    // Tìm FCM token theo userId và deviceId
    @Query("{ 'deletedAt': null, 'userId': ?0, 'deviceId': ?1 }")
    FcmToken findByUserIdAndDeviceId(String userId, String deviceId);

    // Tìm kiếm tất cả thông tin đăng nhập trong hệ thống
    @Query("{ 'deletedAt': null }")
    List<FcmToken> findAllFcmTokens();
}
