package com.microservices.appointment.appointment;

import com.microservices.appointment.utils.PagedResponse;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
@Tag(name = "Appointment")
public class AppointmentController {
    private final AppointmentService service;

    @PostMapping
    public ResponseEntity<Integer> createAppointment(
            @RequestBody @Valid AppointmentRequest request,
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        String token = authorizationHeader != null && authorizationHeader.startsWith("Bearer ")
                ? authorizationHeader.replace("Bearer ", "") : null;
        return ResponseEntity.ok(this.service.createAppointment(request, token));
    }

    @PutMapping
    public ResponseEntity<Void> updateAppointment(
            @RequestBody @Valid AppointmentRequest request,
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        String token = authorizationHeader != null && authorizationHeader.startsWith("Bearer ")
                ? authorizationHeader.replace("Bearer ", "") : null;
        this.service.updateAppointment(request, token);
        return ResponseEntity.accepted().build();
    }

    @GetMapping
    public ResponseEntity<PagedResponse<AppointmentResponse>> findAll(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "limit", defaultValue = "10") int limit) {
        return ResponseEntity.ok(this.service.findAllAppointments(page, limit));
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
