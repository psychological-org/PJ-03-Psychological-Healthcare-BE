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

    /**
     * Gửi notification đơn giản tới 1 device token
     */
    public String sendToToken(String token, String title, String body, Integer appointmentId) throws FirebaseMessagingException {
        Message.Builder messageBuilder = Message.builder()
                .setToken(token)
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build());

        if (appointmentId != null) {
            messageBuilder.putData("appointment_id", String.valueOf(appointmentId));
        }

        Message message = messageBuilder.build();
        String response = messaging.send(message);
        log.info("FCM message sent to token {}: {}", token, response);
        return response;
        // gửi synchronous
//        return messaging.send(msg);
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

    /**
     * Gửi data-only message
     */
    public String sendDataMessage(String deviceToken, Map<String,String> data) throws Exception {
        Message msg = Message.builder()
                .setToken(deviceToken)
                .putAllData(data)
                .build();
        return messaging.send(msg);
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
