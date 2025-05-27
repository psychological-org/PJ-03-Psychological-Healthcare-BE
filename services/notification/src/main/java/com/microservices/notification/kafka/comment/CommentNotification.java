package com.microservices.notification.kafka.comment;

public record CommentNotification(
        Integer id,
        String content,
        String imageUrl,
        String userId,
        Integer postId,
        Integer reactCount) {

}