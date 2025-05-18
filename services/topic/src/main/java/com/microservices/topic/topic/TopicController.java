package com.microservices.topic.topic;

import com.microservices.topic.utils.PagedResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/topics")
@RequiredArgsConstructor
public class TopicController {
    private final TopicService service;

    @PostMapping
    public ResponseEntity<Integer> createTopic(
            @RequestBody @Valid TopicRequest request) {
        return ResponseEntity.ok(this.service.createTopic(request));
    }

    @PutMapping
    public ResponseEntity<Void> updateTopic(
            @RequestBody @Valid TopicRequest request) {
        this.service.updateTopic(request);
        return ResponseEntity.accepted().build();
    }

    @GetMapping
    public ResponseEntity<PagedResponse<TopicResponse>> findAll(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "limit", defaultValue = "10") int limit
    ) {
        return ResponseEntity.ok(this.service.findAllTopics(page, limit));
    }

    @GetMapping("/exists/{topic-id}")
    public ResponseEntity<Boolean> existsById(
            @PathVariable("topic-id") Integer userId) {
        return ResponseEntity.ok(this.service.existsById(userId));
    }

    @GetMapping("/{topic-id}")
    public ResponseEntity<TopicResponse> findById(
            @PathVariable("topic-id") Integer userId) {
        return ResponseEntity.ok(this.service.findById(userId));
    }

    @DeleteMapping("/{topic-id}")
    public ResponseEntity<Void> delete(
            @PathVariable("topic-id") Integer userId) {
        this.service.deleteTopic(userId);
        return ResponseEntity.accepted().build();
    }
}
