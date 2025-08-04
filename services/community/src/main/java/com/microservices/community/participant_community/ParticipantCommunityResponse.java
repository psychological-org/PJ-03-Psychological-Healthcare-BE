package com.microservices.community.participant_community;

import com.microservices.community.user.UserResponse;

public record ParticipantCommunityResponse(
        Integer id,
        String userId,
        Integer communityId,
        UserResponse user
) {}
