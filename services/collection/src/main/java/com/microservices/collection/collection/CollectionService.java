package com.microservices.collection.collection;

import java.util.List;
import java.util.stream.Collectors;

import com.microservices.collection.topic.TopicClient;
import com.microservices.collection.utils.PagedResponse;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.microservices.collection.exception.CollectionNotFoundException;

import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;

@Slf4j
@Service
@RequiredArgsConstructor
public class CollectionService {

    private final CollectionRepository repository;
    private final CollectionMapper mapper;
    private final TopicClient topicClient;

    public Integer createCollection(CollectionRequest request) {
        try {
            var topicResponse = topicClient.findTopicById(request.topicId());
            if (topicResponse == null || topicResponse.getBody() == null) {
                throw new CollectionNotFoundException(
                        String.format("Cannot create collection:: No topic found with ID: %s", request.topicId()));
            }
        } catch (FeignException.FeignClientException e) {
            throw new CollectionNotFoundException("Topic service not available or returned error: " + e.getMessage());
        }
        var collection = this.repository.save(mapper.toCollection(request));
        return collection.getId();
    }

    public void updateCollection(CollectionRequest request) {
        var collection = this.repository.findOneById(request.id())
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
        if (request.topicId() != null) {
            try {
                var topicResponse = topicClient.findTopicById(request.topicId());
                if (topicResponse == null || topicResponse.getBody() == null) {
                    throw new CollectionNotFoundException(
                            String.format("Cannot update collection:: No topic found with ID: %s", request.topicId()));
                }
                collection.setTopicId(request.topicId());
            } catch (FeignException.FeignClientException e) {
                throw new CollectionNotFoundException("Topic service not available or returned error: " + e.getMessage());
            }
        }
    }

    public PagedResponse<CollectionResponse> findAllCollections(int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit);
        Page<Collection> appointments = this.repository.findAllCollections(pageable);
        if (appointments.getContent().isEmpty()) {
            throw new CollectionNotFoundException("No topic found");
        }
        List<CollectionResponse> appointmentResponses = appointments.getContent()
                .stream()
                .map(this.mapper::fromCollection)
                .collect(Collectors.toList());
        return new PagedResponse<>(appointmentResponses, appointments.getTotalPages(), appointments.getTotalElements());
    }

    public CollectionResponse findById(Integer id) {
        return this.repository.findOneById(id)
                .map(mapper::fromCollection)
                .orElseThrow(() -> new CollectionNotFoundException(
                        String.format("No Collection found with the provided ID: %s", id)));
    }

    public boolean existsById(Integer id) {
        return this.repository.findOneById(id)
                .isPresent();
    }

    public void deleteCollection(Integer id) {
        this.repository.softDeleteById(id);
    }

    public List<CollectionResponse> findCollectionsByTopicId(Integer topicId) {
        try {
            var topicResponse = topicClient.findTopicById(topicId);
            if (topicResponse == null || topicResponse.getBody() == null) {
                throw new CollectionNotFoundException(
                        String.format("Cannot find collections:: No topic found with ID: %s", topicId));
            }
        } catch (FeignException.FeignClientException e) {
            throw new CollectionNotFoundException("Topic service not available or returned error: " + e.getMessage());
        }
        List<Collection> collections = repository.findAllCollectionsByTopicId(topicId);
        log.info("Collections found: {}", collections);
        if (collections.isEmpty()) {
            throw new CollectionNotFoundException(
                    String.format("Cannot find collections:: No collections found with topic ID: %s", topicId)
            );
        }
        List<CollectionResponse> result = collections.stream()
                .map(this.mapper::fromCollection)
                .collect(Collectors.toList());
        return result;
    }
}
