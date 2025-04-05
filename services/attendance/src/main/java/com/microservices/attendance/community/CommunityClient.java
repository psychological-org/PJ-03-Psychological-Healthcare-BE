package com.microservices.attendance.community;

import java.util.Optional;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "topic-service", url = "${application.config.topic-url}")
public interface CommunityClient {

    @GetMapping("/{topic-id}")
    Optional<CommunityResponse> findCommunityById(@PathVariable("topic-id") String topicId);
}
