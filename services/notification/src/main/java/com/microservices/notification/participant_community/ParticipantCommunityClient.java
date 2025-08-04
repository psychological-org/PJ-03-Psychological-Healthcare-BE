package com.microservices.notification.participant_community;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "community-service", url = "${application.config.attendance-url}")
public interface ParticipantCommunityClient {
    @GetMapping("/community/users/{communityId}")
    public ResponseEntity<List<ParticipantCommunityResponse>> CommunityIdNotPaginate(
            @PathVariable("communityId") Integer communityId);
}
