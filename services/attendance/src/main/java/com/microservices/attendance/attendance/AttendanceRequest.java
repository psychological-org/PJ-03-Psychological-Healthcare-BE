package com.microservices.attendance.attendance;

import jakarta.validation.constraints.NotNull;

public record AttendanceRequest(
        Integer id,
        @NotNull(message = "Attendance userId is required") Integer userId,
        @NotNull(message = "Attendance communityId is required") Integer communityId) {

}