package com.microservices.collection.collection;

public record CollectionRequest(
        Integer id,
        String name,
        String type,
        String resourceUrl,
        Integer topicId) {}