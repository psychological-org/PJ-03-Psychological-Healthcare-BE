package com.microservices.collection.collection;

public record CollectionResponse(
        Integer id,
        String name,
        String content,
        String avatarUrl,
        Integer topicId) {

}
