package com.microservices.notification.participant_community;

public record ParticipantCommunityResponse(
        Integer id,
        String userId,
        Integer communityId
) {}
