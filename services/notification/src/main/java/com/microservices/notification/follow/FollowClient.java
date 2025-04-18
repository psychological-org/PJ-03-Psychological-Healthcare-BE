package com.microservices.notification.follow;
import java.util.List;
import java.util.Optional;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "follow-service", path = "/api/v1/follows")
public interface FollowClient {

    @GetMapping("/service/friends/{user-id}")
    ResponseEntity<List<FollowResponse>> findAllFriendByUserIdNotPaginate(@PathVariable("user-id") String userId);
}
