package com.microservices.post.post;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.microservices.post.exception.PostNotFoundException;

import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository repository;
    private final PostMapper mapper;

    public Integer createPost(PostRequest request) {
        var post = this.repository.save(mapper.toPost(request));
        return post.getId();
    }

    public void updatePost(PostRequest request) {
        var post = this.repository.findById(request.id())
                .orElseThrow(() -> new PostNotFoundException(
                        String.format("Cannot update post:: No post found with the provided ID: %s",
                                request.id())));
        mergePost(post, request);
        this.repository.save(post);
    }

    private void mergePost(Post post, PostRequest request) {
        if (StringUtils.isNotBlank(request.content())) {
            post.setContent(request.content());
        }
        if (request.communityId() != null) {
            post.setCommunityId(request.communityId());
        }
        if (request.userId() != null) {
            post.setUserId(request.userId());
        }
    }

    public List<PostResponse> findAllPosts() {
        return this.repository.findAll()
                .stream()
                .map(this.mapper::fromPost)
                .collect(Collectors.toList());
    }

    public PostResponse findById(Integer id) {
        return this.repository.findById(id)
                .map(mapper::fromPost)
                .orElseThrow(() -> new PostNotFoundException(
                        String.format("No Post found with the provided ID: %s", id)));
    }

    public boolean existsById(Integer id) {
        return this.repository.findById(id)
                .isPresent();
    }

    public void deletePost(Integer id) {
        this.repository.deleteById(id);
    }
}
