package com.microservices.notification.attendance;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "attendance-service", url = "${application.config.attendance-url}")
public interface AttendanceClient {

    @GetMapping("/community/{community-id}/users")
    ResponseEntity<List<AttendanceResponse>> findUsersByCommunityId(@PathVariable("community-id") Integer communityId);
}
