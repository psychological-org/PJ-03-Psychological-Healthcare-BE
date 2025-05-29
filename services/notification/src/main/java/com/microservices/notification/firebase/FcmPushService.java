package com.microservices.notification.firebase;

import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.AndroidNotification;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class FcmPushService {

    private final FirebaseMessaging messaging;

    public FcmPushService(FirebaseMessaging messaging) {
        this.messaging = messaging;
    }

    /**
     * Gửi notification đơn giản tới 1 device token
     */
    public String sendToToken(String deviceToken, String title, String body) throws Exception {
        Message msg = Message.builder()
                .setToken(deviceToken)
                .setAndroidConfig(AndroidConfig.builder()
                        .setNotification(AndroidNotification.builder()
                                .setTitle(title)
                                .setBody(body)
                                .build())
                        .build())
                .build();

        // gửi synchronous
        return messaging.send(msg);
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
}
