package com.microservices.notification.user_notification;

import com.microservices.notification.exception.UserNotFoundException;
import com.microservices.notification.exception.UserNotificationNotFoundException;
import com.microservices.notification.notification.NotificationResponse;
import com.microservices.notification.notification.NotificationService;
import com.microservices.notification.user.UserClient;
import com.microservices.notification.user.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserNotificationService {
    private UserNotificationRepository userNotificationRepository;
    private UserNotificationMapper userNotificationMapper;
    private UserClient userClient;
    private NotificationService notificationService;

    public String createUserNotification(UserNotificationRequest userNotificationRequest) {
        UserResponse user = userClient.findById(userNotificationRequest.userId()).getBody();
        if (user == null) {
            throw new UserNotFoundException(
                    String.format("Cannot create community:: No user found with ID: %s", user.id()));
        }
        NotificationResponse notification = notificationService.findOneById(userNotificationRequest.notificationId());
        UserNotification userNotification = userNotificationMapper.toUserNotification(userNotificationRequest);
        UserNotification savedUserNotification = userNotificationRepository.save(userNotification);
        return userNotificationMapper.fromUserNotification(savedUserNotification).id();
    }

    public UserNotificationResponse findByUserId(String userId) {
        UserNotification userNotification = userNotificationRepository.findByUserId(userId);
        if (userNotification == null) {
            throw new UserNotificationNotFoundException(
                    String.format("User has never received a notification from the system."));
        }
        return userNotificationMapper.fromUserNotification(userNotification);
    }

    public UserNotificationResponse findByUserIdAndNotificationId(String userId, String notificationId) {
        UserNotification userNotification = userNotificationRepository.findByUserIdAndNotificationId(userId, notificationId);
        if (userNotification == null) {
            throw new UserNotificationNotFoundException(
                    String.format("User has never received a notification from the system."));
        }
        return userNotificationMapper.fromUserNotification(userNotification);
    }

    public UserNotificationResponse findOneById(String id) {
        UserNotification userNotification = userNotificationRepository.findOneById(id);
        if (userNotification == null) {
            throw new UserNotificationNotFoundException(
                    String.format("User has never received a notification from the system."));
        }
        return userNotificationMapper.fromUserNotification(userNotification);
    }

}
