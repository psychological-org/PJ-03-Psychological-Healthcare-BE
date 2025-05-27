package com.microservices.collection.collection;

import java.util.List;

import com.microservices.collection.utils.PagedResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/collections")
@RequiredArgsConstructor
public class CollectionController {
    private final CollectionService service;

    @PostMapping
    public ResponseEntity<Integer> createCollection(
            @RequestBody @Valid CollectionRequest request) {
        return ResponseEntity.ok(this.service.createCollection(request));
    }

    @PutMapping
    public ResponseEntity<Void> updateCollection(
            @RequestBody @Valid CollectionRequest request) {
        this.service.updateCollection(request);
        return ResponseEntity.accepted().build();
    }

    @GetMapping
    public ResponseEntity<PagedResponse<CollectionResponse>> findAll(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "limit", defaultValue = "10") int limit
    ) {
        return ResponseEntity.ok(this.service.findAllCollections(page, limit));
    }

    @GetMapping("/exists/{collection-id}")
    public ResponseEntity<Boolean> existsById(
            @PathVariable("collection-id") Integer userId) {
        return ResponseEntity.ok(this.service.existsById(userId));
    }

    @GetMapping("/{collection-id}")
    public ResponseEntity<CollectionResponse> findById(
            @PathVariable("collection-id") Integer userId) {
        return ResponseEntity.ok(this.service.findById(userId));
    }

    @DeleteMapping("/{collection-id}")
    public ResponseEntity<Void> delete(
            @PathVariable("collection-id") Integer userId) {
        this.service.deleteCollection(userId);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/topic/{topic-id}")
    public ResponseEntity<List<CollectionResponse>> findByTopicId(
            @PathVariable("topic-id") Integer topicId
    ) {
        return ResponseEntity.ok(this.service.findCollectionsByTopicId(topicId));
    }
}

