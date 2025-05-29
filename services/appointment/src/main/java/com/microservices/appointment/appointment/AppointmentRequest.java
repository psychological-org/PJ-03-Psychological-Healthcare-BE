package com.microservices.appointment.appointment;

import java.time.LocalDate;
import java.time.LocalTime;

public record AppointmentRequest(
        Integer id,
        String status,
        LocalDate appointmentDate,
        LocalTime appointmentTime,
        Double rating,
        String review,
        String patientId,
        String doctorId) {

}