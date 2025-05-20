package com.microservices.notification.fcm_token;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "user-service-fcm", url = "${application.config.user-url}/fcm-token")
public interface FcmTokenClient {
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<FcmTokenResponse>> findByUserId(@PathVariable("userId") String userId);
}
