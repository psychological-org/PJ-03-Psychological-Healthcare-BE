package com.microservices.appointment.kafka;

import com.microservices.appointment.appointment.AppointmentResponse;
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
public class AppointmentProducer {
    private final KafkaTemplate<String, AppointmentResponse> kafkaTemplate;

    public void sendAppointmentConfirmationEmail(AppointmentResponse appointmentResponse, String token) {
        log.info("Producing the message to appointment-topic Topic:: {}", appointmentResponse);
        log.info("Authorization token: {}", token);
        Message<AppointmentResponse> message = MessageBuilder
                .withPayload(appointmentResponse)
                .setHeader(TOPIC, "appointment-topic")
                .setHeader("Authorization", "Bearer " + token)
                .build();
        kafkaTemplate.send(message);
    }
}
