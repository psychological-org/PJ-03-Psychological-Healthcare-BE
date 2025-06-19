package com.microservices.appointment.appointment;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.microservices.appointment.exception.UserNotFoundException;
import com.microservices.appointment.kafka.AppointmentProducer;
import com.microservices.appointment.user.UserClient;
import com.microservices.appointment.user.UserResponse;
import com.microservices.appointment.utils.PagedResponse;
import feign.FeignException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.microservices.appointment.exception.AppointmentNotFoundException;

import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository repository;
    private final AppointmentMapper mapper;
    private final UserClient userClient;
    private final AppointmentProducer appointmentProducer;

    public Integer createAppointment(AppointmentRequest request) {
        try {
            Optional<UserResponse> doctorOptional = userClient.findUserById(request.doctorId());
            if (doctorOptional.isEmpty())
                throw new UserNotFoundException("Doctor not found with ID: " + request.doctorId());
            // UserResponse doctorResponse = doctorOptional.get();

            Optional<UserResponse> patientOptional = userClient.findUserById(request.patientId());
            if (patientOptional.isEmpty())
                throw new UserNotFoundException("Patient not found with ID: " + request.patientId());
            // UserResponse patientResponse = patientOptional.get();

            var appointment = this.repository.save(mapper.toAppointment(request));
            appointmentProducer.sendAppointmentConfirmationEmail(mapper.fromAppointment(appointment));

            return appointment.getId();

        } catch (FeignException e) {
            throw new UserNotFoundException("User service not available or returned error: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error when creating appointment", e);
        }
    }


    public void updateAppointment(AppointmentRequest request) {
        var appointment = this.repository.findOneById(request.id())
                .orElseThrow(() -> new AppointmentNotFoundException(
                        String.format("Cannot update appointment:: No appointment found with the provided ID: %s",
                                request.id())));
        mergeAppointment(appointment, request);

        appointmentProducer.sendAppointmentConfirmationEmail(mapper.fromAppointment(appointment));
        this.repository.save(appointment);
    }

    private void mergeAppointment(Appointment appointment, AppointmentRequest request) {
        if (StringUtils.isNotBlank(request.status())) {
            appointment.setStatus(request.status());
        }
        if (request.appointmentDate() != null) {
            appointment.setAppointmentDate(request.appointmentDate());
        }
        if (request.appointmentTime() != null) {
            appointment.setAppointmentTime(request.appointmentTime());
        }
        if (request.rating() != null) {
            appointment.setRating(request.rating());
        }
        if (StringUtils.isNotBlank(request.review())) {
            appointment.setReview(request.review());
        }
        if (request.patientId() != null) {
            Optional<UserResponse> patientOptional = userClient.findUserById(request.patientId());
            if (patientOptional.isEmpty())
                throw new UserNotFoundException("Patient not found with ID: " + request.patientId());
            // UserResponse patientResponse = patientOptional.get();
            appointment.setPatientId(request.patientId());
        }
        if (request.doctorId() != null) {
            Optional<UserResponse> doctorOptional = userClient.findUserById(request.doctorId());
            if (doctorOptional.isEmpty())
                throw new UserNotFoundException("Doctor not found with ID: " + request.doctorId());
            // UserResponse doctorResponse = doctorOptional.get();
            appointment.setDoctorId(request.doctorId());
        }
    }

    public PagedResponse<AppointmentResponse> findAllAppointments(int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit);
        Page<Appointment> appointments = this.repository.findAll(pageable);
        if (appointments.getContent().isEmpty()) {
            throw new AppointmentNotFoundException("No appointment found");
        }
        List<AppointmentResponse> appointmentResponses = appointments.getContent()
                .stream()
                .map(this.mapper::fromAppointment)
                .collect(Collectors.toList());
        return new PagedResponse<>(appointmentResponses, appointments.getTotalPages(), appointments.getTotalElements());
    }

    public AppointmentResponse findById(Integer id) {
        return this.repository.findOneById(id)
                .map(mapper::fromAppointment)
                .orElseThrow(() -> new AppointmentNotFoundException(
                        String.format("No Appointment found with the provided ID: %s", id)));
    }

    public boolean existsById(Integer id) {
        return this.repository.findOneById(id)
                .isPresent();
    }

    public void deleteAppointment(Integer id) {
        this.repository.deleteById(id);
    }
}
