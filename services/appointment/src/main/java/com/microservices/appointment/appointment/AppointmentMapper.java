package com.microservices.appointment.appointment;

import org.springframework.stereotype.Service;

@Service
public class AppointmentMapper {

    public Appointment toAppointment(AppointmentRequest request) {
        if (request == null) {
            return null;
        }
        return Appointment.builder()
                .id(request.id())
                .status(request.status())
                .appointmentDate(request.appointmentDate())
                .appointmentTime(request.appointmentTime())
                .rating(request.rating())
                .review(request.review())
                .patientId(request.patientId())
                .doctorId(request.doctorId())
                .note(request.note())
                .build();
    }

    public AppointmentResponse fromAppointment(Appointment appointment) {
        if (appointment == null) {
            return null;
        }
        return new AppointmentResponse(
                appointment.getId(),
                appointment.getStatus(),
                appointment.getAppointmentDate(),
                appointment.getAppointmentTime(),
                appointment.getRating(),
                appointment.getReview(),
                appointment.getPatientId(),
                appointment.getDoctorId(),
                appointment.getNote());

    }

}
