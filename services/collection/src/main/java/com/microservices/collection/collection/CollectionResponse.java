package com.microservices.collection.collection;

public record CollectionResponse(
        Integer id,
        String name,
        String type,
        String resourceUrl,
        Integer topicId ) {}
