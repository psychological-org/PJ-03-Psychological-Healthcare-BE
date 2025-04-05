package com.microservices.topic.topic;

import jakarta.validation.constraints.NotNull;

public record TopicRequest(
        Integer id,
        @NotNull(message = "Topic name is required") String name,
        @NotNull(message = "Topic content is required") String content,
        @NotNull(message = "Topic avatarUrl is required") String avatarUrl) {

}