package com.microservices.collection.collection;

import jakarta.validation.constraints.NotNull;

public record CollectionRequest(
        Integer id,
        @NotNull(message = "Collection name is required") String name,
        @NotNull(message = "Collection content is required") String type,
        @NotNull(message = "Collection avatarUrl is required") String resourceUrl,
        @NotNull(message = "Collection topicId is required") Integer topicId) {

}