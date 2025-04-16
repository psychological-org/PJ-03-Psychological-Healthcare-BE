package com.microservices.collection.collection_seen;

import com.microservices.collection.collection.CollectionService;
import com.microservices.collection.exception.CollectionNotFoundException;
import com.microservices.collection.exception.UserNotFoundException;
import com.microservices.collection.user.UserClient;
import com.microservices.collection.user.UserResponse;
import com.microservices.collection.utils.PagedResponse;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CollectionSeenService {
    private final CollectionSeenRepository collectionSeenRepository;
    private final CollectionService collectionService;
    private final CollectionSeenMapper collectionSeenMapper;
    private final UserClient userClient;

//    try {
//        var topicResponse = topicClient.findTopicById(request.topicId());
//        if (topicResponse == null || topicResponse.getBody() == null) {
//            throw new CollectionNotFoundException(
//                    String.format("Cannot create collection:: No topic found with ID: %s", request.topicId()));
//        }
//    } catch (FeignException.FeignClientException e) {
//        throw new CollectionNotFoundException("Topic service not available or returned error: " + e.getMessage());
//    }
//    var collection = this.repository.save(mapper.toCollection(request));
//        return collection.getId();

    public Integer createCollectionSeen(CollectionSeenRequest request) {
        try {
            // Kiểm tra User tồn tại
            UserResponse user;
            try {
                ResponseEntity<UserResponse> response = userClient.findById(request.userId());
                if (response == null || response.getBody() == null) {
                    throw new UserNotFoundException("User not found with ID: " + request.userId());
                }
                user = response.getBody();
            } catch (FeignException.NotFound e) {
                throw new UserNotFoundException("User not found with ID: " + request.userId());
            }

            // Kiểm tra Collection tồn tại
            var collectionResponse = collectionService.findById(request.collectionId());
            if (collectionResponse == null) {
                throw new CollectionNotFoundException("Collection not found with ID: " + request.collectionId());
            }

            var collectionSeen = this.collectionSeenMapper.toCollectionSeen(request);
            return collectionSeenRepository.save(collectionSeen).getId();

        } catch (UserNotFoundException | CollectionNotFoundException e) {
            throw e; // ném lại lỗi cụ thể
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error when creating community_seen: " + e.getMessage(), e);
        }
    }


    public void updateCollectionSeen(CollectionSeenRequest request) {
        CollectionSeen collectionSeen = this.collectionSeenRepository.findOneById(request.id())
                .orElseThrow(() -> new UserNotFoundException(
                        String.format("Cannot update collection_seen:: No collection_seen found with the provided ID: %s",
                                request.id())));
        mergeCollectionSeen(collectionSeen, request);
        this.collectionSeenRepository.save(collectionSeen);
    }

    private void mergeCollectionSeen(CollectionSeen collectionSeen, CollectionSeenRequest request) {
        if (request.userId() != null) {
            try {
                ResponseEntity<UserResponse> response = userClient.findById(request.userId());
                if (response == null || response.getBody() == null) {
                    throw new UserNotFoundException(
                            String.format("Cannot update collection_seen:: No user found with ID: %s", request.userId()));
                }
            } catch (FeignException.FeignClientException e) {
                throw new UserNotFoundException("User service not available or returned error: " + e.getMessage());
            }
            collectionSeen.setUserId(request.userId());
        }
        if (request.collectionId() != null) {
            var collectionResponse = collectionService.findById(request.collectionId());
            collectionSeen.setCollectionId(request.collectionId());
        }
    }

    public CollectionSeenResponse findById(Integer id) {
        return this.collectionSeenRepository.findOneById(id)
                .map(collectionSeenMapper::fromCollectionSeenResponse)
                .orElseThrow(() -> new CollectionNotFoundException(
                        String.format("No collection_seen found with the provided ID: %s", id)));
    }

     public boolean existsById(Integer id) {
        CollectionSeenResponse collection = this.findById(id);
        return collection != null;
    }

    public void deleteById(Integer id) {
        CollectionSeenResponse collectionSeen = this.findById(id);
        if (collectionSeen == null) {
            throw new CollectionNotFoundException(
                    String.format("Cannot delete collection_seen:: No collection_seen found with the provided ID: %s",
                            id));
        }
        this.collectionSeenRepository.softDeleteById(id);
    }

    public PagedResponse <CollectionSeenResponse> findAllCollectionSeen(int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit);
        Page<CollectionSeen> appointments = this.collectionSeenRepository.findAll(pageable);
        if (appointments.getContent().isEmpty()) {
            throw new CollectionNotFoundException("No collection found");
        }
        List<CollectionSeenResponse> appointmentResponses = appointments.getContent()
                .stream()
                .map(this.collectionSeenMapper::fromCollectionSeenResponse)
                .collect(Collectors.toList());
        return new PagedResponse<>(appointmentResponses, appointments.getTotalPages(), appointments.getTotalElements());
    }

    public PagedResponse<CollectionSeenResponse> findByUserId(String userId, int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit);
        Page<CollectionSeen> appointments = this.collectionSeenRepository.findByUserId(userId, pageable);
        if (appointments.getContent().isEmpty()) {
            throw new CollectionNotFoundException("No collection found");
        }
        List<CollectionSeenResponse> appointmentResponses = appointments.getContent()
                .stream()
                .map(this.collectionSeenMapper::fromCollectionSeenResponse)
                .collect(Collectors.toList());
        return new PagedResponse<>(appointmentResponses, appointments.getTotalPages(), appointments.getTotalElements());
    }
}
