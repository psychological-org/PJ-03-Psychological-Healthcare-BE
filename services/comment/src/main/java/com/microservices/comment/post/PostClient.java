package com.microservices.comment.post;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "post-service", url = "${application.config.post-url}")
public interface PostClient {
    @GetMapping("/{post-id}")
    ResponseEntity<PostResponse> findById(@PathVariable("post-id") Integer userId);
}


