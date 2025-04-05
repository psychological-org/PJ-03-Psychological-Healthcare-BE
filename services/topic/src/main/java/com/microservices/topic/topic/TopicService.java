package com.microservices.topic.topic;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.microservices.topic.exception.TopicNotFoundException;

import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TopicService {

    private final TopicRepository repository;
    private final TopicMapper mapper;

    public Integer createTopic(TopicRequest request) {
        var topic = this.repository.save(mapper.toTopic(request));
        return topic.getId();
    }

    public void updateTopic(TopicRequest request) {
        var topic = this.repository.findById(request.id())
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

    public List<TopicResponse> findAllTopics() {
        return this.repository.findAll()
                .stream()
                .map(this.mapper::fromTopic)
                .collect(Collectors.toList());
    }

    public TopicResponse findById(Integer id) {
        return this.repository.findById(id)
                .map(mapper::fromTopic)
                .orElseThrow(() -> new TopicNotFoundException(
                        String.format("No Topic found with the provided ID: %s", id)));
    }

    public boolean existsById(Integer id) {
        return this.repository.findById(id)
                .isPresent();
    }

    public void deleteTopic(Integer id) {
        this.repository.deleteById(id);
    }
}
