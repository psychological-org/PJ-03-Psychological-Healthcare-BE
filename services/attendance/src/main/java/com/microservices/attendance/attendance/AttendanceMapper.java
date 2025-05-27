package com.microservices.attendance.attendance;

import org.springframework.stereotype.Service;

@Service
public class AttendanceMapper {

    public Attendance toAttendance(AttendanceRequest request) {
        if (request == null) {
            return null;
        }
        return Attendance.builder()
                .id(request.id())
                .userId(request.userId())
                .communityId(request.communityId())
                .build();
    }

    public AttendanceResponse fromAttendance(Attendance attendance) {
        if (attendance == null) {
            return null;
        }
        return new AttendanceResponse(
                attendance.getId(),
                attendance.getUserId(),
                attendance.getCommunityId());

    }

}
