package com.microservices.follow.follow;

import org.springframework.stereotype.Service;

@Service
public class FollowMapper {

    public Follow toFollow(FollowRequest request) {
        if (request == null) {
            return null;
        }
        return Follow.builder()
                .id(request.id())
                .status(request.status())
                .senderId(request.senderId())
                .receiverId(request.receiverId())
                .build();
    }

    public FollowResponse fromFollow(Follow follow) {
        if (follow == null) {
            return null;
        }
        return new FollowResponse(
                follow.getId(),
                follow.getStatus(),
                follow.getSenderId(),
                follow.getReceiverId());

    }

}
