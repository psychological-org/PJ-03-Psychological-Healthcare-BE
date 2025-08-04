package com.microservices.notification.firebase;

import com.google.firebase.messaging.*;
import org.springframework.stereotype.Service;

import java.util.Map;
import static org.apache.kafka.common.requests.DeleteAclsResponse.log;


import static org.apache.kafka.common.requests.DeleteAclsResponse.log;

@Service
public class FcmPushService {

    private final FirebaseMessaging messaging;

    public FcmPushService(FirebaseMessaging messaging) {
        this.messaging = messaging;
    }

    public String sendToToken(String token, String title, String body, Integer appointmentId, String userNotificationId, String role) throws FirebaseMessagingException {
        Message.Builder messageBuilder = Message.builder()
                .setToken(token)
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build());

        if (appointmentId != null) {
            messageBuilder.putData("appointment_id", String.valueOf(appointmentId));
        }
        if (userNotificationId != null) {
            messageBuilder.putData("userNotificationId", userNotificationId);
        }
        if (role != null) {
            messageBuilder.putData("role", role);
        }

        Message message = messageBuilder.build();
        String response = messaging.send(message);
        log.info("FCM message sent to token {}: {}", token, response);
        return response;
    }

    public String sendToToken(String token, String title, String body) throws FirebaseMessagingException {
        Message message = Message.builder()
                .setToken(token)
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .build();

        String response = messaging.send(message);
        log.info("FCM message sent to token {}: {}", token, response);
        return response;
    }

    public String sendDataMessage(String deviceToken, Map<String,String> data) throws FirebaseMessagingException {
        Message msg = Message.builder()
                .setToken(deviceToken)
                .putAllData(data)
                .build();
        String response = messaging.send(msg);
        log.info("FCM data message sent to token {}: {}", deviceToken, response);
        return response;
    }

    public String sendToTokenWithData(String token, String title, String body, Map<String, String> data) throws FirebaseMessagingException {
        Message message = Message.builder()
                .setToken(token)
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .putAllData(data)
                .build();
        String response = messaging.send(message);
        log.info("FCM message with notification and data sent to token {}: {}", token, response);
        return response;
    }
}