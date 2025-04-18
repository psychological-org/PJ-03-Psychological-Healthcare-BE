package com.microservices.topic.topic;

import java.util.List;
import java.util.stream.Collectors;

import com.microservices.topic.utils.PagedResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.microservices.topic.exception.TopicNotFoundException;

import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TopicService {

    private final TopicRepository repository;
    private final TopicMapper mapper;
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(TopicService.class);

    public Integer createTopic(TopicRequest request) {
        var topic = this.repository.save(mapper.toTopic(request));
        return topic.getId();
    }

    public void updateTopic(TopicRequest request) {
        var topic = this.repository.findOneById(request.id())
                .orElseThrow(() -> new TopicNotFoundException(
                        String.format("Cannot update topic:: No topic found with the provided ID: %s",
                                request.id())));
        mergeTopic(topic, request);
        this.repository.save(topic);
    }

    private void mergeTopic(Topic topic, TopicRequest request) {
        if (StringUtils.isNotBlank(request.name())) {
            topic.setName(request.name());
        }
        if (StringUtils.isNotBlank(request.content())) {
            topic.setContent(request.content());
        }
        if (request.avatarUrl() != null) {
            topic.setAvatarUrl(request.avatarUrl());
        }
    }

    public PagedResponse<TopicResponse> findAllTopics(int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit);
        Page<Topic> appointments = this.repository.findAllTopics(pageable);
        if (appointments.getContent().isEmpty()) {
            throw new TopicNotFoundException("No topic found");
        }
        List<TopicResponse> appointmentResponses = appointments.getContent()
                .stream()
                .map(this.mapper::fromTopic)
                .collect(Collectors.toList());
        return new PagedResponse<>(appointmentResponses, appointments.getTotalPages(), appointments.getTotalElements());
    }

    public TopicResponse findById(Integer id) {
        return this.repository.findOneById(id)
                .map(mapper::fromTopic)
                .orElseThrow(() -> new TopicNotFoundException(
                        String.format("No Topic found with the provided ID: %s", id)));
    }

    public boolean existsById(Integer id) {
        return this.repository.findOneById(id)
                .isPresent();
    }

    public void deleteTopic(Integer id) {
        this.repository.softDeleteById(id);
    }
}
