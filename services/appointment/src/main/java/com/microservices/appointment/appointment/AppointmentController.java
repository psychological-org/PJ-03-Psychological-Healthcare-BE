package com.microservices.appointment.appointment;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
public class AppointmentController {
    private final AppointmentService service;

    @PostMapping
    public ResponseEntity<Integer> createAppointment(
            @RequestBody @Valid AppointmentRequest request) {
        return ResponseEntity.ok(this.service.createAppointment(request));
    }

    @PutMapping
    public ResponseEntity<Void> updateAppointment(
            @RequestBody @Valid AppointmentRequest request) {
        this.service.updateAppointment(request);
        return ResponseEntity.accepted().build();
    }

    @GetMapping
    public ResponseEntity<List<AppointmentResponse>> findAll() {
        return ResponseEntity.ok(this.service.findAllAppointments());
    }

    @GetMapping("/exists/{appointment-id}")
    public ResponseEntity<Boolean> existsById(
            @PathVariable("appointment-id") Integer userId) {
        return ResponseEntity.ok(this.service.existsById(userId));
    }

    @GetMapping("/{appointment-id}")
    public ResponseEntity<AppointmentResponse> findById(
            @PathVariable("appointment-id") Integer userId) {
        return ResponseEntity.ok(this.service.findById(userId));
    }

    @DeleteMapping("/{appointment-id}")
    public ResponseEntity<Void> delete(
            @PathVariable("appointment-id") Integer userId) {
        this.service.deleteAppointment(userId);
        return ResponseEntity.accepted().build();
    }
}
