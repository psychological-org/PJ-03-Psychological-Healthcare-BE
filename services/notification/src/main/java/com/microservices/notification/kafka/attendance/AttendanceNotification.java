package com.microservices.notification.kafka.attendance;

public record AttendanceNotification(
        Integer notificationId,
        Integer senderId,
        Integer receiverId) { 
}