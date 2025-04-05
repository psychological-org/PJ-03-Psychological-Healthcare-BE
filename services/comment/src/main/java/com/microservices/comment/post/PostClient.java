package com.microservices.comment.post;

import java.util.Optional;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "post-service", url = "${application.config.post-url}")
public interface PostClient {

    @GetMapping("/{post-id}")
    Optional<PostResponse> findPostById(@PathVariable("post-id") String userId);
}
