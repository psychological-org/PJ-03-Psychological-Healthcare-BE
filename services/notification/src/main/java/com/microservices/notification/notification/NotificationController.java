package com.microservices.notification.notification;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.microservices.notification.firebase.FcmPushService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

//    @PostMapping("/send")
//    public ResponseEntity<String> sendNotification(String token, String title, String body) {
//        try {
////            String token = "f2wqDbi3SRiJPhpM_VuCX_:APA91bGgGqCZ3W0M_kwvbHN_witw_CFirjwPSeFmeoYtItceWrq2giE8y8uUcx-DFABTzfLoOsUfrv1qcu68dqW3tAJKvX7sv4G5WIuBFWc6mttvevCAFsI";
////            String title = "Test Notification";
////            String body = "This is a test notification";
//            String response = fcmPushService.sendToToken(token, title, body);
//            return ResponseEntity.ok("Notification sent. FCM ID: " + response);
//        } catch (FirebaseMessagingException e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("Failed to send notification: " + e.getMessage());
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }

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
