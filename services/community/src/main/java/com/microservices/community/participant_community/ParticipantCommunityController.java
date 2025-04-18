package com.microservices.community.participant_community;

import com.microservices.community.utils.PagedResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/communities/participant_community")
@RequiredArgsConstructor
public class ParticipantCommunityController {
    private final ParticipantCommunityService service;

    @PostMapping
    public ResponseEntity<Integer> createParticipantCommunity(@RequestBody @Valid ParticipantCommunityRequest request) {
        return ResponseEntity.ok(this.service.createParticipantCommunity(request));
    }

    @GetMapping("{id}")
    public ResponseEntity<ParticipantCommunityResponse> getParticipantCommunityById(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(this.service.findById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<PagedResponse<ParticipantCommunityResponse>> getParticipantCommunityByUserId(
            @PathVariable("userId") String userId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "limit", defaultValue = "10") int limit) {
        return ResponseEntity.ok(this.service.findByUserId(userId, page, limit));
    }

    @GetMapping("/community/{communityId}")
    public ResponseEntity<PagedResponse<ParticipantCommunityResponse>> getParticipantCommunityByCommunityId(
            @PathVariable("communityId") Integer communityId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "limit", defaultValue = "10") int limit) {
        return ResponseEntity.ok(this.service.findByCommunityId(communityId, page, limit));
    }

    // Get all participant communities
    @GetMapping
    public ResponseEntity<PagedResponse<ParticipantCommunityResponse>> getAllParticipantCommunities(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "limit", defaultValue = "10") int limit) {
        return ResponseEntity.ok(this.service.findAllParticipantCommunity(page, limit));
    }

    @GetMapping("/exists/{id}")
    public ResponseEntity<Boolean> existsById(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(this.service.existsById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteParticipantCommunityById(@PathVariable("id") Integer id) {
        // Check if the participant community exists
        if (!this.service.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        this.service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping
    public ResponseEntity<Void> updateParticipantCommunity(@RequestBody @Valid ParticipantCommunityRequest request) {
        this.service.updateParticipantCommunity(request);
        return ResponseEntity.accepted().build();
    }
}
