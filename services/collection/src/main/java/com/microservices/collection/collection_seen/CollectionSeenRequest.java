package com.microservices.collection.collection_seen;

public record CollectionSeenRequest(
        Integer id,
        String userId,
        Integer collectionId

) {
}
