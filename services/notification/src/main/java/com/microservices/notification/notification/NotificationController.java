package com.microservices.notification.notification;

import brave.Request;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private NotificationService notificationService;

    @PostMapping
    public ResponseEntity<String> createNotification(@RequestBody NotificationRequest request) {
        notificationService.createNotification(request);
        return ResponseEntity.ok(this.notificationService.createNotification(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificationResponse> findOneById(@PathVariable String id) {
        return ResponseEntity.ok(notificationService.findOneById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable String id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }
}
