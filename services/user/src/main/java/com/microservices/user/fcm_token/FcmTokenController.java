package com.microservices.user.fcm_token;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users/fcm-token")
@RequiredArgsConstructor
public class FcmTokenController {
    private final FcmTokenService fcmTokenService;

    @PostMapping()
    public String createFcmToken(@RequestBody FcmTokenRequest request) {
        return fcmTokenService.createFcmToken(request);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FcmTokenResponse> findOneById(@PathVariable("id") String id) {
        return ResponseEntity.ok(fcmTokenService.findOneById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<FcmTokenResponse>> findByUserId(@PathVariable("userId") String userId) {
        return ResponseEntity.ok(fcmTokenService.findByUserId(userId));
    }

    @GetMapping("/user/{userId}/device/{deviceId}")
    public ResponseEntity<FcmTokenResponse> findByUserIdAndDeviceId(
            @PathVariable("userId") String userId,
            @PathVariable("deviceId") String deviceId
    ) {
        return ResponseEntity.ok(fcmTokenService.findByUserIdAndDeviceId(userId, deviceId));
    }

    @GetMapping("/user/{userId}/device/{deviceId}/fcm-token/{fcmToken}")
    public ResponseEntity<FcmTokenResponse> findByUserIdAndDeviceIdAndFcmToken(
            @PathVariable("userId") String userId,
            @PathVariable("deviceId") String deviceId,
            @PathVariable("fcmToken") String fcmToken
    ) {
        return ResponseEntity.ok(fcmTokenService.findByUserIdAndDeviceIdAndFcmToken(userId, deviceId, fcmToken));
    }

    @PutMapping()
    public ResponseEntity<Void> updateFcmToken(@RequestBody FcmTokenRequest request) {
        fcmTokenService.updateFcmToken(request);
        return ResponseEntity.accepted().build();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteFcmToken(@PathVariable("id") String id) {
        fcmTokenService.deleteFcmToken(id);
        return ResponseEntity.accepted().build();
    }
}
