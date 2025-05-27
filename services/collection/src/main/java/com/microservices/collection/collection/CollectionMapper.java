package com.microservices.collection.collection;

import org.springframework.stereotype.Service;

@Service
public class CollectionMapper {

    public Collection toCollection(CollectionRequest request) {
        if (request == null) {
            return null;
        }
        return Collection.builder()
                .id(request.id())
                .name(request.name())
                .type(request.type())
                .resourceUrl(request.resourceUrl())
                .topicId(request.topicId())
                .build();
    }

    public CollectionResponse fromCollection(Collection collection) {
        if (collection == null) {
            return null;
        }
        return new CollectionResponse(
                collection.getId(),
                collection.getName(),
                collection.getType(),
                collection.getResourceUrl(),
                collection.getTopicId());
                
    }

}
