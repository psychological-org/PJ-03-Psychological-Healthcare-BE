package com.microservices.topic.topic;

import org.springframework.stereotype.Service;

@Service
public class TopicMapper {

    public Topic toTopic(TopicRequest request) {
        if (request == null) {
            return null;
        }
        return Topic.builder()
                .id(request.id())
                .name(request.name())
                .content(request.content())
                .avatarUrl(request.avatarUrl())
                .build();
    }

    public TopicResponse fromTopic(Topic topic) {
        if (topic == null) {
            return null;
        }
        return new TopicResponse(
                topic.getId(),
                topic.getName(),
                topic.getContent(),
                topic.getAvatarUrl());
    }

}
