package com.microservices.community.community;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.microservices.community.exception.CommunityNotFoundException;

import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommunityService {

    private final CommunityRepository repository;
    private final CommunityMapper mapper;

    public Integer createCommunity(CommunityRequest request) {
        var community = this.repository.save(mapper.toCommunity(request));
        return community.getId();
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
    }

    public List<CommunityResponse> findAllCommunitys() {
        return this.repository.findAll()
                .stream()
                .map(this.mapper::fromCommunity)
                .collect(Collectors.toList());
    }

    public CommunityResponse findById(Integer id) {
        return this.repository.findById(id)
                .map(mapper::fromCommunity)
                .orElseThrow(() -> new CommunityNotFoundException(
                        String.format("No Community found with the provided ID: %s", id)));
    }

    public boolean existsById(Integer id) {
        return this.repository.findById(id)
                .isPresent();
    }

    public void deleteCommunity(Integer id) {
        this.repository.deleteById(id);
    }
}
