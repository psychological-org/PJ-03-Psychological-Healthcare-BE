package com.microservices.collection.collection_seen;

import com.microservices.collection.collection.CollectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CollectionSeenMapper {
    private final CollectionService collectionService;


    public CollectionSeen toCollectionSeen(CollectionSeenRequest request) {
        if (request == null) {
            return null;
        }
        return CollectionSeen.builder()
                .id(request.id())
                .userId(request.userId())
                .collectionId(request.collectionId())
                .build();
    }

    public CollectionSeenResponse fromCollectionSeenResponse(CollectionSeen collectionSeen) {
        if (collectionSeen == null) {
            return null;
        }
        return new CollectionSeenResponse(
                collectionSeen.getId(),
                collectionSeen.getUserId(),
                collectionSeen.getCollectionId()
        );
    }
}
