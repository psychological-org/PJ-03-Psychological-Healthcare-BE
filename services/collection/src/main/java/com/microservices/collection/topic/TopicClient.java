package com.microservices.collection.topic;

import java.util.Optional;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "topic-service", url = "${application.config.topic-url}")
public interface TopicClient {

    @GetMapping("/{topic-id}")
    Optional<TopicResponse> findTopicById(@PathVariable("topic-id") String topicId);
}
