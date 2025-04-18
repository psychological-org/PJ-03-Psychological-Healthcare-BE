package com.microservices.post.community;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "community-service", path = "/api/v1/communities")
public interface CommunityClient {
    @GetMapping("/{community-id}")
    ResponseEntity<CommunityResponse> findById(@PathVariable("community-id") Integer userId);
}



//@GetMapping("/{community-id}")
//public ResponseEntity<CommunityResponse> findById(
//        @PathVariable("community-id") Integer userId) {
//    return ResponseEntity.ok(this.service.findById(userId));
//}