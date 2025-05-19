package com.microservices.notification.notification;

import com.microservices.notification.exception.NotificationNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

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

}
