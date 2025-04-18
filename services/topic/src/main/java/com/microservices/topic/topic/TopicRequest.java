package com.microservices.topic.topic;

import jakarta.validation.constraints.NotNull;

public record TopicRequest(
        Integer id,
        String name,
        String content,
        String avatarUrl) {}