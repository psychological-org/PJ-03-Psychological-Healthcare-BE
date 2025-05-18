package com.microservices.notification.user_notification;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications/user-notification")
@RequiredArgsConstructor
public class UserNotificationController {
    private UserNotificationService userNotificationService;

    @PostMapping
    public ResponseEntity<Void> createUserNotification(UserNotificationRequest request) {
        this.userNotificationService.createUserNotification(request);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserNotificationResponse> findOneById(@PathVariable("id") String id) {
        return ResponseEntity.ok(this.userNotificationService.findOneById(id));
    }

    @GetMapping("/user/{user-id}")
    public ResponseEntity<UserNotificationResponse> findAllByUserId(@PathVariable("user-id") String userId) {
        return ResponseEntity.ok(this.userNotificationService.findByUserId(userId));
    }

    @GetMapping("/user/{user-id}/notification/{notification-id}")
    public ResponseEntity<UserNotificationResponse> findByUserIdAndNotificationId(
            @PathVariable("user-id") String userId,
            @PathVariable("notification-id") String notificationId) {
        return ResponseEntity.ok(this.userNotificationService.findByUserIdAndNotificationId(userId, notificationId));
    }
}
