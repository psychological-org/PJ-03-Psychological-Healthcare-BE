package com.microservices.topic.topic;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<List<TopicResponse>> findAll() {
        return ResponseEntity.ok(this.service.findAllTopics());
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
