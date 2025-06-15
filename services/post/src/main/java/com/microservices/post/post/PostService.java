package com.microservices.post.post;

import java.util.List;
import java.util.stream.Collectors;

import com.microservices.post.community.CommunityClient;
import com.microservices.post.community.CommunityResponse;
import com.microservices.post.exception.CommunityNotFoundException;
import com.microservices.post.exception.UserNotFoundException;
import com.microservices.post.kafka.PostProducer;
import com.microservices.post.user.UserClient;
import com.microservices.post.user.UserResponse;
import com.microservices.post.utils.PagedResponse;
import feign.FeignException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.microservices.post.exception.PostNotFoundException;

import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;

import static org.apache.kafka.common.requests.DeleteAclsResponse.log;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository repository;
    private final PostMapper mapper;
    private final UserClient userClient;
    private final CommunityClient communityClient;
    private final PostProducer postProducer;

    public Integer createPost(PostRequest request) {
        log.info("Processing createPost for userId: {}, communityId: {}",
                request.userId(), request.communityId());
        log.info("Current authentication: {}",
                SecurityContextHolder.getContext().getAuthentication());
        try {
            // Kiểm tra user tồn tại (không dùng biến user)
            var userResponse = userClient.findById(request.userId());
            if (userResponse == null || userResponse.getBody() == null) {
                throw new UserNotFoundException("User not found with ID: " + request.userId());
            }

            // Kiểm tra community tồn tại (không dùng biến community)
            var communityResponse = communityClient.findById(request.communityId());
            if (communityResponse == null || communityResponse.getBody() == null) {
                throw new CommunityNotFoundException("Community not found with ID: " + request.communityId());
            }

            var post = this.repository.save(mapper.toPost(request));
            postProducer.sendNotificationOfNewPost(mapper.fromPost(post));
            return post.getId();

        } catch (FeignException.Unauthorized e) {
            throw new RuntimeException("Unauthorized access to community service: " + e.getMessage(), e);
        } catch (FeignException.NotFound e) {
            throw new CommunityNotFoundException("Community not found with ID: " + request.communityId());
        } catch (Exception e) {
            throw new RuntimeException("Error creating post: " + e.getMessage(), e);
        }
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
        // Kiểm tra và cập nhật userId
        if (request.userId() != null) {
            try {
                ResponseEntity<UserResponse> userResponse = userClient.findById(request.userId());
                if (userResponse != null) {
                    UserResponse userBody = userResponse.getBody();
                    if (userBody != null) {
                        post.setUserId(userBody.id());
                    } else {
                        throw new UserNotFoundException("User response body is null for ID: " + request.userId());
                    }
                } else {
                    throw new UserNotFoundException("User response is null for ID: " + request.userId());
                }
            } catch (FeignException.NotFound e) {
                throw new UserNotFoundException("User not found with ID: " + request.userId());
            } catch (Exception e) {
                throw new RuntimeException("An error occurred while updating the post userId: " + e.getMessage(), e);
            }
        }

        // Cập nhật nội dung nếu không rỗng
        if (StringUtils.isNotBlank(request.content())) {
            post.setContent(request.content());
        }

        // Kiểm tra và cập nhật communityId
        if (request.communityId() != null) {
            try {
                ResponseEntity<CommunityResponse> communityResponse = communityClient.findById(request.communityId());
                if (communityResponse != null) {
                    CommunityResponse communityBody = communityResponse.getBody();
                    if (communityBody != null) {
                        post.setCommunityId(request.communityId()); // Nếu bạn có trường này trong Post
                    } else {
                        throw new CommunityNotFoundException(
                                "Community response body is null for ID: " + request.communityId());
                    }
                } else {
                    throw new CommunityNotFoundException("Community response is null for ID: " + request.communityId());
                }
            } catch (FeignException.NotFound e) {
                throw new CommunityNotFoundException("Community not found with ID: " + request.communityId());
            } catch (Exception e) {
                throw new RuntimeException("An error occurred while updating the post communityId: " + e.getMessage(),
                        e);
            }
        }

        // Cập nhật các thuộc tính khác nếu có
        if (StringUtils.isNotBlank(request.imageUrl())) {
            post.setImageUrl(request.imageUrl());
        }

        if (StringUtils.isNotBlank(request.visibility())) {
            post.setVisibility(request.visibility());
        }

        if (request.reactCount() != null) {
            post.setReactCount(request.reactCount());
        }
    }

    public PagedResponse<PostResponse> findAllPosts(int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Post> posts = this.repository.findAllUsers(pageable);
        if (posts.getContent().isEmpty()) {
            throw new PostNotFoundException("No post found");
        }
        List<PostResponse> postResponses = posts.getContent()
                .stream()
                .map(this.mapper::fromPost)
                .collect(Collectors.toList());
        return new PagedResponse<>(postResponses, posts.getTotalPages(), posts.getTotalElements());
    }

    public PostResponse findById(Integer id) {
        return this.repository.findOneById(id)
                .map(mapper::fromPost)
                .orElseThrow(() -> new PostNotFoundException(
                        String.format("No Post found with the provided ID: %s", id)));
    }

    public boolean existsById(Integer id) {
        return this.repository.findOneById(id)
                .isPresent();
    }

    public void deletePost(Integer id) {
        this.repository.softDeleteById(id);
    }

    public PagedResponse<PostResponse> findPostsByUserId(String userId, int page, int limit) {
        try {
            ResponseEntity<UserResponse> userResponse = userClient.findById(userId);
            if (userResponse == null || userResponse.getBody() == null) {
                throw new UserNotFoundException("User not found with ID: " + userId);
            }

            Pageable pageable = PageRequest.of(page, limit);
            Page<Post> postPage = repository.findByUserIdAndDeletedAtIsNull(userId, pageable);
            List<PostResponse> responses = postPage.getContent().stream()
                    .map(mapper::fromPost)
                    .collect(Collectors.toList());
            return new PagedResponse<>(responses, postPage.getTotalPages(), postPage.getTotalElements());
        } catch (FeignException e) {
            throw new UserNotFoundException("User service not available or returned error: " + e.getMessage());
        }
    }

    public PagedResponse<PostResponse> findPostsByCommunityId(Integer communityId, int page, int limit) {
        try {
            ResponseEntity<CommunityResponse> communityResponse = communityClient.findById(communityId);
            if (communityResponse == null || communityResponse.getBody() == null) {
                throw new CommunityNotFoundException("Community not found with ID: " + communityId);
            }

            Pageable pageable = PageRequest.of(page, limit);
            Page<Post> postPage = repository.findByCommunityIdAndDeletedAtIsNull(communityId, pageable);
            List<PostResponse> responses = postPage.getContent().stream()
                    .map(mapper::fromPost)
                    .collect(Collectors.toList());
            return new PagedResponse<>(responses, postPage.getTotalPages(), postPage.getTotalElements());
        } catch (FeignException e) {
            throw new CommunityNotFoundException("Community service not available or returned error: " + e.getMessage());
        }
    }
}
