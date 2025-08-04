package com.microservices.notification.user_notification;

import com.microservices.notification.exception.NotificationNotFoundException;
import com.microservices.notification.exception.UserNotFoundException;
import com.microservices.notification.exception.UserNotificationNotFoundException;
import com.microservices.notification.notification.NotificationResponse;
import com.microservices.notification.notification.NotificationService;
import com.microservices.notification.user.UserClient;
import com.microservices.notification.user.UserResponse;
import com.microservices.notification.utils.PagedResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.apache.kafka.common.requests.DeleteAclsResponse.log;

@Service
@RequiredArgsConstructor
public class UserNotificationService {
    private final UserNotificationRepository userNotificationRepository;
    private final UserNotificationMapper userNotificationMapper;
    private final UserClient userClient;
    private final NotificationService notificationService;

    public String createUserNotification(UserNotificationRequest userNotificationRequest) {
        UserResponse user = userClient.findById(userNotificationRequest.userId()).getBody();
        if (user == null) {
            throw new UserNotFoundException(
                    String.format("Cannot create community:: No user found with ID: %s",
                            userNotificationRequest.userId()));
        }
        NotificationResponse notification = notificationService.findOneById(userNotificationRequest.notificationId());
        log.info("Notification fetched for ID {}: {}", userNotificationRequest.notificationId(), notification);
        if (notification == null) {
            throw new NotificationNotFoundException(
                    String.format("Notification not found with ID: %s", userNotificationRequest.notificationId()));
        }
        UserNotification userNotification = userNotificationMapper.toUserNotification(userNotificationRequest);
        UserNotification savedUserNotification = userNotificationRepository.save(userNotification);
        log.info("Saved UserNotification: {}", savedUserNotification);
        return userNotificationMapper.fromUserNotification(savedUserNotification).id();
    }

    public PagedResponse<UserNotificationResponse> findByUserId(String userId, int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit);
        Page<UserNotification> userNotificationPage = userNotificationRepository.findByUserId(userId, pageable);

        if (userNotificationPage.isEmpty()) {
            throw new UserNotificationNotFoundException(
                    String.format("No notifications found for user ID: %s", userId));
        }

        List<UserNotificationResponse> userNotificationResponses = userNotificationPage.getContent()
                .stream()
                .map(userNotificationMapper::fromUserNotification)
                .collect(Collectors.toList());

        return new PagedResponse<>(userNotificationResponses, userNotificationPage.getTotalPages(),
                userNotificationPage.getTotalElements());
    }


    public UserNotificationResponse findByUserIdAndNotificationId(String userId, String notificationId) {
        UserNotification userNotification = userNotificationRepository.findByUserIdAndNotificationId(userId,
                notificationId);
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

//    public PagedResponse<PostResponse> findAllPosts(int page, int limit) {
//        Pageable pageable = PageRequest.of(page, limit);
//        Page<Post> appointments = this.repository.findAll(pageable);
//        if (appointments.getContent().isEmpty()) {
//            throw new PostNotFoundException("No post found");
//        }
//        List<PostResponse> appointmentResponses = appointments.getContent()
//                .stream()
//                .map(this.mapper::fromPost)
//                .collect(Collectors.toList());
//        return new PagedResponse<>(appointmentResponses, appointments.getTotalPages(), appointments.getTotalElements());
//    }

    // Tìm tất cả thông báo
    public PagedResponse<UserNotificationResponse> findAll(int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit);
        Page<UserNotification> userNotificationPage = userNotificationRepository.findAll(pageable);
        if (userNotificationPage.getContent().isEmpty()) {
            throw new UserNotificationNotFoundException("No user notification found");
        }
        List<UserNotificationResponse> userNotificationResponses = userNotificationPage.getContent()
                .stream()
                .map(userNotificationMapper::fromUserNotification)
                .collect(Collectors.toList());
        return new PagedResponse<>(userNotificationResponses, userNotificationPage.getTotalPages(),
                userNotificationPage.getTotalElements());
    }

}
