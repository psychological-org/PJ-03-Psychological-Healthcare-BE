package com.microservices.notification.attendance;

public record AttendanceResponse(
        Integer communityId,
        String userId
) {
}
