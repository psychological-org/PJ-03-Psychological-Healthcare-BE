package com.microservices.comment.kafka;

import com.microservices.comment.comment.CommentResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import static org.springframework.kafka.support.KafkaHeaders.TOPIC;


@Setter
@Getter
@Service
@Slf4j
@RequiredArgsConstructor
public class CommentProducer {
    private final KafkaTemplate<String, CommentResponse> kafkaTemplate;

    public void sendNotificationOfNewCommentToPost(CommentResponse appointmentResponse) {
        log.info("Producing the message to comment-topic Topic:: {}", appointmentResponse);
        Message<CommentResponse> message = MessageBuilder
                .withPayload(appointmentResponse)
                .setHeader(TOPIC, "comment-topic")
                .build();
        kafkaTemplate.send(message);
    }
}