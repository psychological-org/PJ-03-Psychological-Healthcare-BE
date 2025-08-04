package com.microservices.user.fcm_token;

public record FcmTokenResponse(
        String id,
        String fcmToken,
        String deviceId,
        String deviceType,
        String userId
) {
    public String toString() {
        return "FcmTokenResponse{" +
                "id='" + id + '\'' +
                ", fcmToken='" + fcmToken + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", deviceType='" + deviceType + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }
}
