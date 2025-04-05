package com.microservices.collection.collection;

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
    public ResponseEntity<List<CollectionResponse>> findAll() {
        return ResponseEntity.ok(this.service.findAllCollections());
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
}
