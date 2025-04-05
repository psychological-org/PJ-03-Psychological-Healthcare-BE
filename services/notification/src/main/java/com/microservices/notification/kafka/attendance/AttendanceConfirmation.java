package com.microservices.notification.kafka.attendance;

public record AttendanceConfirmation(
        Integer notificationId,
        Integer senderId,
        Integer receiverId) { 
}