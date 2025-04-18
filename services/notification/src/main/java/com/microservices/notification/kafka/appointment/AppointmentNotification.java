package com.microservices.notification.kafka.appointment;

import java.time.LocalDate;
import java.time.LocalTime;

public record AppointmentNotification(
        Integer id,
        String status,
        LocalDate appointmentDate,
        LocalTime appointmentTime,
        Double rating,
        String review,
        String patientId,
        String doctorId) {
}
