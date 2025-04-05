package com.microservices.appointment.appointment;

import jakarta.validation.constraints.NotNull;

public record AppointmentRequest(
                Integer id,
                @NotNull(message = "Appointment status is required") String status,
                @NotNull(message = "Appointment date is required") String appointmentDate,
                @NotNull(message = "Appointment time is required") String appointmentTime,
                @NotNull(message = "Appointment patientId is required") Integer patientId,
                @NotNull(message = "Appointment doctorId is required") Integer doctorId) {

}