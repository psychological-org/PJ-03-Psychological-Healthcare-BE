package com.microservices.collection.collection_seen;

import com.microservices.collection.utils.PagedResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/collections/collection-seen")
@RequiredArgsConstructor
public class CollectionSeenController {
    private final CollectionSeenService service;

    @PostMapping
    public ResponseEntity<Integer> createCollectionSeen(
            @RequestBody @Valid CollectionSeenRequest request) {
        return ResponseEntity.ok(this.service.createCollectionSeen(request));
    }

    // get collections by userId
    @GetMapping("/users/{userId}")
    public ResponseEntity<PagedResponse<CollectionSeenResponse>> getCollectionSeenByUserId(
            @PathVariable("userId") String userId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "limit", defaultValue = "10") int limit) {
        return ResponseEntity.ok(this.service.findByUserId(userId ,page, limit));
    }

    // get collectionSeen by id
    @GetMapping("/{id}")
    public ResponseEntity<CollectionSeenResponse> getCollectionSeenById(
            @PathVariable("id") Integer id) {
        return ResponseEntity.ok(this.service.findById(id));
    }

    // get all collectionSeen
    @GetMapping
    public ResponseEntity<PagedResponse<CollectionSeenResponse>> getAllCollectionSeen(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "limit", defaultValue = "10") int limit) {
        return ResponseEntity.ok(this.service.findAllCollectionSeen(page, limit));
    }

    // delete collectionSeen by id
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCollectionSeenById(
            @PathVariable("id") Integer id) {
        this.service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // update collectionSeen by id
    @PutMapping
    public ResponseEntity<Void> updateCollectionSeenById(
            @RequestBody @Valid CollectionSeenRequest request) {
        this.service.updateCollectionSeen(request);
        return ResponseEntity.accepted().build();
    }

    // check if collectionSeen exists by id
    @GetMapping("/exists/{id}")
    public ResponseEntity<Boolean> existsById(
            @PathVariable("id") Integer id) {
        return ResponseEntity.ok(this.service.existsById(id));
    }
}
