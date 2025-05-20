package com.microservices.notification.notification;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.microservices.notification.exception.NotificationNotFoundException;
import com.microservices.notification.fcm_token.FcmTokenResponse;
import com.microservices.notification.firebase.FcmPushService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final FcmPushService fcmPushService;

    public String createNotification(NotificationRequest request) {
        Notification notification = notificationMapper.toNotification(request);
        Notification savedNotification = notificationRepository.save(notification);
        return savedNotification.getId();
    }

    public NotificationResponse findOneById(String id) {
        Notification notification = notificationRepository.findOneById(id)
                .orElseThrow(() -> new NotificationNotFoundException("Notification not found"));
        return notificationMapper.fromNotification(notification);
    }

    // soft delete
    public void deleteNotification(String id) {
        Notification notification = notificationRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new NotificationNotFoundException("Notification not found"));
        notification.setDeletedAt(LocalDateTime.now());
        notificationRepository.save(notification);
    }

    public NotificationResponse findNotificationByName(String name) {
        Notification notification = notificationRepository.findByNameAndDeletedAtIsNull(name);
        if (notification == null) {
            throw new NotificationNotFoundException("Notification not found");
        }
        return notificationMapper.fromNotification(notification);
    }

    public String sendPushNotificationByFirebase(String token, String title, String body) {
        try {
            String response = fcmPushService.sendToToken(token, title, body);
            return response;
        } catch (FirebaseMessagingException e) {
            throw new RuntimeException("Failed to send notification: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
