package com.microservices.community.participant_community;

import com.microservices.community.user.UserClient;
import com.microservices.community.user.UserResponse;
import org.springframework.http.ResponseEntity;
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

    public ParticipantCommunityResponse fromParticipantCommunityResponse(ParticipantCommunity participantCommunity, UserClient userClient) {
        if (participantCommunity == null) {
            return null;
        }
        ResponseEntity<UserResponse> userResponse = userClient.findById(participantCommunity.getUserId());
        UserResponse user = (userResponse != null && userResponse.getBody() != null) ? userResponse.getBody() : null;
        return new ParticipantCommunityResponse(
                participantCommunity.getId(),
                participantCommunity.getUserId(),
                participantCommunity.getCommunityId(),
                user
        );
    }
}
