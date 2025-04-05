package com.microservices.follow.follow;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.microservices.follow.exception.FollowNotFoundException;

import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository repository;
    private final FollowMapper mapper;

    public Integer createFollow(FollowRequest request) {
        var follow = this.repository.save(mapper.toFollow(request));
        return follow.getId();
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
        if (StringUtils.isNotBlank(request.status())) {
            follow.setStatus(request.status());
        }
        if (request.senderId() != null) {
            follow.setSenderId(request.senderId());
        }
        if (request.receiverId() != null) {
            follow.setReceiverId(request.receiverId());
        }
    }

    public List<FollowResponse> findAllFollows() {
        return this.repository.findAll()
                .stream()
                .map(this.mapper::fromFollow)
                .collect(Collectors.toList());
    }

    public FollowResponse findById(Integer id) {
        return this.repository.findById(id)
                .map(mapper::fromFollow)
                .orElseThrow(() -> new FollowNotFoundException(
                        String.format("No Follow found with the provided ID: %s", id)));
    }

    public boolean existsById(Integer id) {
        return this.repository.findById(id)
                .isPresent();
    }

    public void deleteFollow(Integer id) {
        this.repository.deleteById(id);
    }
}
