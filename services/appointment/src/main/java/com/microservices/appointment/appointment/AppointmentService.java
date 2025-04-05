package com.microservices.appointment.appointment;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.microservices.appointment.exception.AppointmentNotFoundException;

import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository repository;
    private final AppointmentMapper mapper;

    public Integer createAppointment(AppointmentRequest request) {
        var appointment = this.repository.save(mapper.toAppointment(request));
        return appointment.getId();
    }

    public void updateAppointment(AppointmentRequest request) {
        var appointment = this.repository.findById(request.id())
                .orElseThrow(() -> new AppointmentNotFoundException(
                        String.format("Cannot update appointment:: No appointment found with the provided ID: %s",
                                request.id())));
        mergeAppointment(appointment, request);
        this.repository.save(appointment);
    }

    private void mergeAppointment(Appointment appointment, AppointmentRequest request) {
        if (StringUtils.isNotBlank(request.status())) {
            appointment.setStatus(request.status());
        }
        if (StringUtils.isNotBlank(request.appointmentDate())) {
            appointment.setAppointmentDate(request.appointmentDate());
        }
        if (StringUtils.isNotBlank(request.appointmentTime())) {
            appointment.setAppointmentTime(request.appointmentTime());
        }
        if (request.patientId() != null) {
            appointment.setPatientId(request.patientId());
        }
        if (request.doctorId() != null) {
            appointment.setDoctorId(request.doctorId());
        }
    }

    public List<AppointmentResponse> findAllAppointments() {
        return this.repository.findAll()
                .stream()
                .map(this.mapper::fromAppointment)
                .collect(Collectors.toList());
    }

    public AppointmentResponse findById(Integer id) {
        return this.repository.findById(id)
                .map(mapper::fromAppointment)
                .orElseThrow(() -> new AppointmentNotFoundException(
                        String.format("No Appointment found with the provided ID: %s", id)));
    }

    public boolean existsById(Integer id) {
        return this.repository.findById(id)
                .isPresent();
    }

    public void deleteAppointment(Integer id) {
        this.repository.deleteById(id);
    }
}
