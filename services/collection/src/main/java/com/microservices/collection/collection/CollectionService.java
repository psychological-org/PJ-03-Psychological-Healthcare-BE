package com.microservices.collection.collection;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.microservices.collection.exception.CollectionNotFoundException;

import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CollectionService {

    private final CollectionRepository repository;
    private final CollectionMapper mapper;

    public Integer createCollection(CollectionRequest request) {
        var collection = this.repository.save(mapper.toCollection(request));
        return collection.getId();
    }

    public void updateCollection(CollectionRequest request) {
        var collection = this.repository.findById(request.id())
                .orElseThrow(() -> new CollectionNotFoundException(
                        String.format("Cannot update collection:: No collection found with the provided ID: %s",
                                request.id())));
        mergeCollection(collection, request);
        this.repository.save(collection);
    }

    private void mergeCollection(Collection collection, CollectionRequest request) {
        if (StringUtils.isNotBlank(request.name())) {
            collection.setName(request.name());
        }
        if (StringUtils.isNotBlank(request.type())) {
            collection.setType(request.type());
        }
        if (request.resourceUrl() != null) {
            collection.setResourceUrl(request.resourceUrl());
        }
    }

    public List<CollectionResponse> findAllCollections() {
        return this.repository.findAll()
                .stream()
                .map(this.mapper::fromCollection)
                .collect(Collectors.toList());
    }

    public CollectionResponse findById(Integer id) {
        return this.repository.findById(id)
                .map(mapper::fromCollection)
                .orElseThrow(() -> new CollectionNotFoundException(
                        String.format("No Collection found with the provided ID: %s", id)));
    }

    public boolean existsById(Integer id) {
        return this.repository.findById(id)
                .isPresent();
    }

    public void deleteCollection(Integer id) {
        this.repository.deleteById(id);
    }
}
