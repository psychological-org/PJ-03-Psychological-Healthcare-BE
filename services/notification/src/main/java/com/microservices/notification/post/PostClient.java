package com.microservices.notification.post;

import com.microservices.notification.user.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "post-service", path = "/api/v1/posts")
public interface PostClient {

    @GetMapping("/{post-id}")
    public ResponseEntity<PostResponse> findById(@PathVariable("post-id") Integer postId);
}
