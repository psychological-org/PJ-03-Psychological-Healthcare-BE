package com.microservices.collection.topic;

import java.util.Optional;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "topic-service", path = "/api/v1/topics")
public interface TopicClient {

    @GetMapping("/{topic-id}")
    ResponseEntity<TopicResponse> findTopicById(@PathVariable("topic-id") Integer topicId);
}


//@GetMapping("/{user-id}")
//public ResponseEntity<UserResponse> findById(@PathVariable("user-id") String userId);