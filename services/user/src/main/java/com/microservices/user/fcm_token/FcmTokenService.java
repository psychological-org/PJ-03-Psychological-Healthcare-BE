package com.microservices.user.fcm_token;

import com.microservices.user.exception.FcmTokenNotFoundException;
import com.microservices.user.user.User;
import com.microservices.user.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FcmTokenService {
    private final FcmTokenRepository fcmTokenRepository;
    private final FcmTokenMapper fcmTokenMapper;
    private final UserService userService;

    public String createFcmToken(FcmTokenRequest request) {
        try {
            User user = userService.findRawByUserId(request.userId());
        } catch (Exception e) {
            throw new FcmTokenNotFoundException("User not found");
        }

        FcmToken response = fcmTokenRepository.findByUserIdAndDeviceIdAndFcmToken(request.userId(),
                request.deviceId(), request.fcmToken());
        if (response != null) {
            return response.getId();
        }

        FcmToken byDevice = fcmTokenRepository.findByUserIdAndDeviceId(
                request.userId(), request.deviceId()
        );
        if (byDevice != null) {
            byDevice.setFcmToken(request.fcmToken());
            return fcmTokenRepository.save(byDevice).getId();
        }

        FcmToken token = fcmTokenMapper.toFcmToken(request);
        return fcmTokenRepository.save(token).getId();
    }

    public FcmTokenResponse findOneById(String id) {
        FcmToken fcmToken = fcmTokenRepository.findByIdAndDeletedAtIsNull(id);
        if (fcmToken == null) {
            throw new FcmTokenNotFoundException("FCM Token not found");
        }
        return fcmTokenMapper.fromNotification(fcmToken);
    }

    public List<FcmTokenResponse> findByUserId(String userId) {
        List<FcmToken> fcmToken = fcmTokenRepository.findByUserId(userId);
        if (fcmToken == null || fcmToken.isEmpty()) {
            throw new FcmTokenNotFoundException("FCM Token not found");
        }
        return fcmToken.stream()
                .map(fcmTokenMapper::fromNotification)
                .toList();
    }

    public FcmTokenResponse findByUserIdAndDeviceId(String userId, String deviceId) {
        FcmToken fcmToken = fcmTokenRepository.findByUserIdAndDeviceId(userId, deviceId);
        if (fcmToken == null) {
            throw new FcmTokenNotFoundException("FCM Token not found");
        }
        return fcmTokenMapper.fromNotification(fcmToken);
    }

    public FcmTokenResponse findByUserIdAndDeviceIdAndFcmToken(String userId, String deviceId, String fcmToken) {
        FcmToken fcmTokenEntity = fcmTokenRepository.findByUserIdAndDeviceIdAndFcmToken(userId, deviceId, fcmToken);
        if (fcmTokenEntity == null) {
            throw new FcmTokenNotFoundException("FCM Token not found");
        }
        return fcmTokenMapper.fromNotification(fcmTokenEntity);
    }

    public void updateFcmToken(FcmTokenRequest request) {
        FcmToken fcmToken = fcmTokenRepository.findByIdAndDeletedAtIsNull(request.id());
        System.out.println("fcmToken = " + fcmToken);
        if (fcmToken == null) {
            throw new FcmTokenNotFoundException("FCM Token not found");
        }
        if (request.fcmToken() != null) {
            fcmToken.setFcmToken(request.fcmToken());
        }
        if (request.deviceId() != null) {
            fcmToken.setDeviceId(request.deviceId());
        }
        if (request.deviceType() != null) {
            fcmToken.setDeviceType(request.deviceType());
        }
        if (request.userId() != null) {
            try {
                User user = userService.findRawByUserId(request.userId());
            } catch (Exception e) {
                throw new FcmTokenNotFoundException("User not found");
            }
            fcmToken.setUserId(request.userId());
        }

        fcmToken.setDeviceId(request.deviceId());
        fcmToken.setDeviceType(request.deviceType());
        fcmToken.setUserId(request.userId());
        fcmTokenRepository.save(fcmToken);
    }

    public void deleteFcmToken(String id) {
        FcmToken fcmToken = fcmTokenRepository.findByIdAndDeletedAtIsNull(id);
        fcmToken.setDeletedAt(LocalDateTime.now());
        fcmTokenRepository.save(fcmToken);
    }
}
