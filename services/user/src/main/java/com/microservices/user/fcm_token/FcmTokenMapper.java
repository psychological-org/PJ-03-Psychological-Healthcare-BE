package com.microservices.user.fcm_token;

import org.springframework.stereotype.Service;

@Service
public class FcmTokenMapper {
    public FcmToken toFcmToken(FcmTokenRequest request) {
        if (request == null) {
            return null;
        }
        return FcmToken.builder()
                .id(request.id())
                .fcmToken(request.fcmToken())
                .deviceId(request.deviceId())
                .deviceType(request.deviceType())
                .userId(request.userId())
                .build();
    }

    public FcmTokenResponse fromNotification(FcmToken fcmToken) {
        if (fcmToken == null) {
            return null;
        }
        return new FcmTokenResponse(
                fcmToken.getId(),
                fcmToken.getFcmToken(),
                fcmToken.getDeviceId(),
                fcmToken.getDeviceType(),
                fcmToken.getUserId()
        );
    }
}
