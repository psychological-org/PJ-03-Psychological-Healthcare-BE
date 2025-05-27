package com.microservices.topic.topic;

public record TopicRequest(
        Integer id,
        String name,
        String content,
        String avatarUrl) {}