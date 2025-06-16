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
import java.util.HashMap;
import java.util.Map;

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

    // Phiên bản cho thông báo lịch hẹn với cả notification và data payload
    public String sendPushNotificationByFirebase(String token, String title, String body, Integer appointmentId, String role) {
        try {
            Map<String, String> data = new HashMap<>();
            if (appointmentId != null) {
                data.put("appointment_id", String.valueOf(appointmentId));
            }
            data.put("role", role);
            String response = fcmPushService.sendToTokenWithData(token, title, body, data);
            return response;
        } catch (Exception e) {
            throw new RuntimeException("Failed to send notification: " + e.getMessage(), e);
        }
    }

    // Phiên bản cho thông báo lịch hẹn
    public String sendPushNotificationByFirebase(String token, String title, String body, Integer appointmentId) {
        try {
            String response = fcmPushService.sendToToken(token, title, body, appointmentId);
            return response;
        } catch (FirebaseMessagingException e) {
            throw new RuntimeException("Failed to send notification: " + e.getMessage(), e);
        }
    }

    public String sendPushNotificationByFirebase(String token, String title, String body) {
        try {
            String response = fcmPushService.sendToToken(token, title, body);
            return response;
        } catch (FirebaseMessagingException e) {
            throw new RuntimeException("Failed to send notification: " + e.getMessage(), e);
        }
    }

}
