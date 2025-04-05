package com.microservices.notification.kafka.appointment;

public record AppointmentConfirmation(
                Integer notificationId,
                Integer senderId,
                Integer receiverId) {
}