package com.microservices.community.community;

import org.springframework.stereotype.Service;

@Service
public class CommunityMapper {

    public Community toCommunity(CommunityRequest request) {
        if (request == null) {
            return null;
        }
        return Community.builder()
                .id(request.id())
                .name(request.name())
                .content(request.content())
                .avatarUrl(request.avatarUrl())
                .adminId(request.adminId())
                .build();
    }

    public CommunityResponse fromCommunity(Community community) {
        if (community == null) {
            return null;
        }
        return new CommunityResponse(
                community.getId(),
                community.getName(),
                community.getContent(),
                community.getAvatarUrl(),
                community.getAdminId());
    }

}
