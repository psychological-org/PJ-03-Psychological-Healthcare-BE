package com.microservices.follow.kafka;

import com.microservices.follow.follow.FollowResponse;
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
public class FollowProducer {
    private final KafkaTemplate<String, FollowResponse> kafkaTemplate;

    public void sendNotificationOfFollowRequest(FollowResponse appointmentResponse) {
        log.info("Producing the message to follow-topic Topic:: {}", appointmentResponse);
        Message<FollowResponse> message = MessageBuilder
                .withPayload(appointmentResponse)
                .setHeader(TOPIC, "follow-topic")
                .build();
        kafkaTemplate.send(message);
    }
}
