package com.microservices.collection.collection_seen;

public record CollectionSeenResponse(
        Integer id,
        String userId,
        Integer collectionId
) {}
