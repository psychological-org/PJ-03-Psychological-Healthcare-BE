package com.microservices.post.kafka;

import com.microservices.post.post.PostResponse;
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
public class PostProducer {
    private final KafkaTemplate<String, PostResponse> kafkaTemplate;

    public void sendNotificationOfNewPost(PostResponse appointmentResponse) {
        log.info("Producing the message to post-topic Topic:: {}", appointmentResponse);
        Message<PostResponse> message = MessageBuilder
                .withPayload(appointmentResponse)
                .setHeader(TOPIC, "post-topic")
                .build();
        kafkaTemplate.send(message);
    }
}