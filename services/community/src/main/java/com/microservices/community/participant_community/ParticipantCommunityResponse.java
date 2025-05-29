package com.microservices.community.participant_community;

public record ParticipantCommunityResponse(
        Integer id,
        String userId,
        Integer communityId
) {}
