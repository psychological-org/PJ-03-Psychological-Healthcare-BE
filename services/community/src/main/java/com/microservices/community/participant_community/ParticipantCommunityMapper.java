package com.microservices.community.participant_community;

import com.microservices.community.community.Community;
import org.springframework.stereotype.Service;

@Service
public class ParticipantCommunityMapper {
    public ParticipantCommunity toParticipantCommunity(ParticipantCommunityRequest request) {
        if (request == null) {
            return null;
        }
        return ParticipantCommunity.builder()
                .id(request.id())
                .userId(request.userId())
                .communityId(request.communityId())
                .build();
    }

    public ParticipantCommunityResponse fromParticipantCommunityResponse(ParticipantCommunity participantCommunity) {
        if (participantCommunity == null) {
            return null;
        }
        return new ParticipantCommunityResponse(
                participantCommunity.getId(),
                participantCommunity.getUserId(),
                participantCommunity.getCommunityId()
        );
    }
}
