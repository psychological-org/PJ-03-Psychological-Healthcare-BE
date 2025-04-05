package com.microservices.appointment.appointment;

public record AppointmentResponse(
        Integer id,
        String status,
        String appointmentDate,
        String appointmentTime,
        Integer patientId,
        Integer doctorId) {

}
