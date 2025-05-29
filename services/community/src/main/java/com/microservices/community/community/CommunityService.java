package com.microservices.community.community;

import java.util.List;
import java.util.stream.Collectors;

import com.microservices.community.exception.UserNotFoundException;
import com.microservices.community.user.UserClient;
import com.microservices.community.user.UserResponse;
import com.microservices.community.utils.PagedResponse;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.microservices.community.exception.CommunityNotFoundException;

import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommunityService {

    private final CommunityRepository repository;
    private final CommunityMapper mapper;
    private final UserClient userClient;

    private static final Logger logger = LoggerFactory.getLogger(CommunityService.class);

    public Integer createCommunity(CommunityRequest request) {
        try {
            ResponseEntity<UserResponse> response = userClient.findById(request.adminId());
            logger.info("User found: " + response);
            if (response == null || response.getBody() == null) {
                throw new UserNotFoundException(
                        String.format("Cannot create community:: No user found with ID: %s", request.adminId()));
            }

            var community = this.repository.save(mapper.toCommunity(request));
            return community.getId();
        } catch (FeignException e) {
            throw new UserNotFoundException("User service not available or returned error: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error when creating community", e);
        }
    }


    public void updateCommunity(CommunityRequest request) {
        var community = this.repository.findById(request.id())
                .orElseThrow(() -> new CommunityNotFoundException(
                        String.format("Cannot update community:: No community found with the provided ID: %s",
                                request.id())));
        mergeCommunity(community, request);
        this.repository.save(community);
    }

    private void mergeCommunity(Community community, CommunityRequest request) {
        if (StringUtils.isNotBlank(request.name())) {
            community.setName(request.name());
        }

        if (StringUtils.isNotBlank(request.content())) {
            community.setContent(request.content());
        }

        if (request.avatarUrl() != null) {
            community.setAvatarUrl(request.avatarUrl());
        }

        if (request.adminId() != null && !request.adminId().equals(community.getAdminId())) {
            try {
                ResponseEntity<UserResponse> response = userClient.findById(request.adminId());
                if (response == null || response.getBody() == null) {
                    throw new UserNotFoundException(
                            String.format("Cannot update community:: No user found with the provided ID: %s",
                                    request.adminId()));
                }
                community.setAdminId(request.adminId());
            } catch (FeignException e) {
                throw new UserNotFoundException("User service not available or returned error: " + e.getMessage());
            } catch (Exception e) {
                throw new RuntimeException("Unexpected error while verifying admin user", e);
            }
        }
    }

    public PagedResponse<CommunityResponse> findAllCommunities(int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit);
        Page<Community> appointments = this.repository.findAll(pageable);
        if (appointments.getContent().isEmpty()) {
            throw new CommunityNotFoundException("No community found");
        }
        List<CommunityResponse> appointmentResponses = appointments.getContent()
                .stream()
                .map(this.mapper::fromCommunity)
                .collect(Collectors.toList());
        return new PagedResponse<>(appointmentResponses, appointments.getTotalPages(), appointments.getTotalElements());
    }

    public CommunityResponse findById(Integer id) {
        return this.repository.findOneById(id)
                .map(mapper::fromCommunity)
                .orElseThrow(() -> new CommunityNotFoundException(
                        String.format("No Community found with the provided ID: %s", id)));
    }

    public boolean existsById(Integer id) {
        return this.repository.findOneById(id)
                .isPresent();
    }

    public void deleteCommunity(Integer id) {
        this.repository.softDeleteById(id);
    }
}
