package com.microservices.comment.comment;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.microservices.comment.exception.CommentNotFoundException;

import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository repository;
    private final CommentMapper mapper;

    public Integer createComment(CommentRequest request) {
        var comment = this.repository.save(mapper.toComment(request));
        return comment.getId();
    }

    public void updateComment(CommentRequest request) {
        var comment = this.repository.findById(request.id())
                .orElseThrow(() -> new CommentNotFoundException(
                        String.format("Cannot update comment:: No comment found with the provided ID: %s",
                                request.id())));
        mergeComment(comment, request);
        this.repository.save(comment);
    }

    private void mergeComment(Comment comment, CommentRequest request) {
        if (StringUtils.isNotBlank(request.content())) {
            comment.setContent(request.content());
        }
        if (StringUtils.isNotBlank(request.content())) {
            comment.setContent(request.content());
        }
        if (request.imageUrl() != null) {
            comment.setImageUrl(request.imageUrl());
        }
    }

    public List<CommentResponse> findAllComments() {
        return this.repository.findAll()
                .stream()
                .map(this.mapper::fromComment)
                .collect(Collectors.toList());
    }

    public CommentResponse findById(Integer id) {
        return this.repository.findById(id)
                .map(mapper::fromComment)
                .orElseThrow(() -> new CommentNotFoundException(
                        String.format("No Comment found with the provided ID: %s", id)));
    }

    public boolean existsById(Integer id) {
        return this.repository.findById(id)
                .isPresent();
    }

    public void deleteComment(Integer id) {
        this.repository.deleteById(id);
    }
}
