package com.microservices.attendance.attendance;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.microservices.attendance.exception.AttendanceNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository repository;
    private final AttendanceMapper mapper;

    public Integer createAttendance(AttendanceRequest request) {
        var attendance = this.repository.save(mapper.toAttendance(request));
        return attendance.getId();
    }

    public void updateAttendance(AttendanceRequest request) {
        var attendance = this.repository.findById(request.id())
                .orElseThrow(() -> new AttendanceNotFoundException(
                        String.format("Cannot update attendance:: No attendance found with the provided ID: %s",
                                request.id())));
        mergeAttendance(attendance, request);
        this.repository.save(attendance);
    }

    private void mergeAttendance(Attendance attendance, AttendanceRequest request) {
        if (request.userId() != null) {
            attendance.setUserId(request.userId());
        }
        if (request.communityId() != null) {
            attendance.setCommunityId(request.communityId());
        }
    }

    public List<AttendanceResponse> findAllAttendances() {
        return this.repository.findAll()
                .stream()
                .map(this.mapper::fromAttendance)
                .collect(Collectors.toList());
    }

    public AttendanceResponse findById(Integer id) {
        return this.repository.findById(id)
                .map(mapper::fromAttendance)
                .orElseThrow(() -> new AttendanceNotFoundException(
                        String.format("No Attendance found with the provided ID: %s", id)));
    }

    public boolean existsById(Integer id) {
        return this.repository.findById(id)
                .isPresent();
    }

    public void deleteAttendance(Integer id) {
        this.repository.deleteById(id);
    }
}
