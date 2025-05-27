package com.microservices.collection.topic;

public record TopicResponse(
        Integer id,
        String name,
        String content,
        String avatarUrl) {
}
