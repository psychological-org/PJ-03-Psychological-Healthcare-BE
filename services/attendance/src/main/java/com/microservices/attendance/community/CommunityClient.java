package com.microservices.attendance.community;

import java.util.Optional;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "community-service", url = "${application.config.community-url}")
public interface CommunityClient {

    @GetMapping("/{community-id}")
    Optional<CommunityResponse> findCommunityById(@PathVariable("community-id") String topicId);
}
