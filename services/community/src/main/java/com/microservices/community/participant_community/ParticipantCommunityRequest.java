package com.microservices.community.participant_community;

public record ParticipantCommunityRequest(
        Integer id,
        String userId,
        Integer communityId
) {}
