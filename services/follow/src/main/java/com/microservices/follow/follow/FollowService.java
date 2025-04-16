package com.microservices.follow.follow;

import java.util.List;
import java.util.stream.Collectors;

import com.microservices.follow.exception.UserNotFoundException;
import com.microservices.follow.kafka.FollowProducer;
import com.microservices.follow.user.UserClient;
import com.microservices.follow.user.UserResponse;
import com.microservices.follow.utils.PagedResponse;
import feign.FeignException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.microservices.follow.exception.FollowNotFoundException;

import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository repository;
    private final FollowMapper mapper;
    private final UserClient userClient;
    private final FollowProducer followProducer;

    public Integer createFollow(FollowRequest request) {
        if (request.receiverId().equals(request.senderId())) {
            throw new IllegalArgumentException("Cannot create follow: Sender and receiver cannot be the same");
        }
        try {
            UserResponse sender;
            try {
                sender = userClient.findById(request.senderId()).getBody();
                if (sender == null) {
                    throw new UserNotFoundException(
                            String.format("Cannot create follow: No sender found with ID: %s", request.senderId()));
                }
            } catch (FeignException.NotFound e) {
                throw new UserNotFoundException(
                        String.format("Cannot create follow: No sender found with ID: %s", request.senderId()));
            }

            UserResponse receiver;
            receiver = userClient.findById(request.receiverId()).getBody();
            if (receiver == null) {
                throw new UserNotFoundException(
                        String.format("Cannot create follow: No receiver found with ID: %s", request.receiverId()));
            }

            var follow = this.repository.save(mapper.toFollow(request));
            // Send follow event to Kafka
            followProducer.sendNotificationOfFollowRequest(mapper.fromFollow(follow));

            return follow.getId();

        } catch (UserNotFoundException e) {
            throw e; // preserve specific user error
        } catch (Exception e) {
            throw new FollowNotFoundException("User service not available or returned error: " + e.getMessage());
        }
    }

    public void updateFollow(FollowRequest request) {
        var follow = this.repository.findById(request.id())
                .orElseThrow(() -> new FollowNotFoundException(
                        String.format("Cannot update follow:: No follow found with the provided ID: %s",
                                request.id())));
        mergeFollow(follow, request);
        this.repository.save(follow);
    }

    private void mergeFollow(Follow follow, FollowRequest request) {
        if (follow.getSenderId().equals(request.receiverId())) {
            throw new IllegalArgumentException("Cannot update follow: Sender and receiver cannot be the same");
        }
        if (StringUtils.isNotBlank(request.status())) {
            follow.setStatus(request.status());
        }
        if (request.senderId() != null) {
            UserResponse sender = userClient.findById(request.senderId()).getBody();
            if (sender == null) {
                throw new UserNotFoundException(
                        String.format("Cannot update follow: No sender found with ID: %s", request.senderId()));
            }
            follow.setSenderId(request.senderId());
        }
        if (request.receiverId() != null) {
            UserResponse receiver = userClient.findById(request.receiverId()).getBody();
            if (receiver == null) {
                throw new UserNotFoundException(
                        String.format("Cannot update follow: No receiver found with ID: %s", request.receiverId()));
            }
            follow.setReceiverId(request.receiverId());
        }
    }

    public PagedResponse<FollowResponse> findAllFollows(int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit);
        Page<Follow> appointments = this.repository.findAll(pageable);
        if (appointments.getContent().isEmpty()) {
            throw new FollowNotFoundException("No follow found");
        }
        List<FollowResponse> appointmentResponses = appointments.getContent()
                .stream()
                .map(this.mapper::fromFollow)
                .collect(Collectors.toList());
        return new PagedResponse<>(appointmentResponses, appointments.getTotalPages(), appointments.getTotalElements());
    }

    public PagedResponse<FollowResponse> findAllFriendByUserId(String userId, int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit);
        Page<Follow> appointments = this.repository.findAllFollowByUserId(userId, pageable);
        if (appointments.getContent().isEmpty()) {
            throw new FollowNotFoundException("No follow found");
        }
        List<FollowResponse> appointmentResponses = appointments.getContent()
                .stream()
                .map(this.mapper::fromFollow)
                .collect(Collectors.toList());
        return new PagedResponse<>(appointmentResponses, appointments.getTotalPages(), appointments.getTotalElements());
    }

    public PagedResponse<FollowResponse> findAllFriendRequestByUserId(String userId, int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit);
        Page<Follow> appointments = this.repository.findAllRequestByUserId(userId, pageable);
        if (appointments.getContent().isEmpty()) {
            throw new FollowNotFoundException("No follow found");
        }
        List<FollowResponse> appointmentResponses = appointments.getContent()
                .stream()
                .map(this.mapper::fromFollow)
                .collect(Collectors.toList());
        return new PagedResponse<>(appointmentResponses, appointments.getTotalPages(), appointments.getTotalElements());
    }

    public List<FollowResponse> findAllFriendRequestByUserIdNotPaginate(String userId) {
        List<Follow> appointments = this.repository.findAllFollowByUserIdNotPaginate(userId);
        if (appointments.isEmpty()) {
            throw new FollowNotFoundException("No follow found");
        }
        List<FollowResponse> appointmentResponses = appointments
                .stream()
                .map(this.mapper::fromFollow)
                .collect(Collectors.toList());
        return appointmentResponses;
    }

    public FollowResponse findById(Integer id) {
        return this.repository.findById(id)
                .map(mapper::fromFollow)
                .orElseThrow(() -> new FollowNotFoundException(
                        String.format("No Follow found with the provided ID: %s", id)));
    }

    public boolean existsById(Integer id) {
        FollowResponse follow = this.findById(id);
        return follow != null;
    }

    public void deleteFollow(Integer id) {
        this.repository.findById(id)
                .orElseThrow(() -> new FollowNotFoundException(
                        String.format("Cannot delete follow:: No follow found with the provided ID: %s", id)));
        this.repository.softDeleteById(id);
    }
}
