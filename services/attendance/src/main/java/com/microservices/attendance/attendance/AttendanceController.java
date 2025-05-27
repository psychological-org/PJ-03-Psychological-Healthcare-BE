package com.microservices.attendance.attendance;

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
@RequestMapping("/api/v1/attendances")
@RequiredArgsConstructor
public class AttendanceController {
    private final AttendanceService service;

    @PostMapping
    public ResponseEntity<Integer> createAttendance(
            @RequestBody @Valid AttendanceRequest request) {
        return ResponseEntity.ok(this.service.createAttendance(request));
    }

    @PutMapping
    public ResponseEntity<Void> updateAttendance(
            @RequestBody @Valid AttendanceRequest request) {
        this.service.updateAttendance(request);
        return ResponseEntity.accepted().build();
    }

    @GetMapping
    public ResponseEntity<List<AttendanceResponse>> findAll() {
        return ResponseEntity.ok(this.service.findAllAttendances());
    }

    @GetMapping("/exists/{attendance-id}")
    public ResponseEntity<Boolean> existsById(
            @PathVariable("attendance-id") Integer userId) {
        return ResponseEntity.ok(this.service.existsById(userId));
    }

    @GetMapping("/{attendance-id}")
    public ResponseEntity<AttendanceResponse> findById(
            @PathVariable("attendance-id") Integer userId) {
        return ResponseEntity.ok(this.service.findById(userId));
    }

    @DeleteMapping("/{attendance-id}")
    public ResponseEntity<Void> delete(
            @PathVariable("attendance-id") Integer userId) {
        this.service.deleteAttendance(userId);
        return ResponseEntity.accepted().build();
    }
}
