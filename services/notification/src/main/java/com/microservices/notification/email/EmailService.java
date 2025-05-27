package com.microservices.notification.email;

import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import com.microservices.notification.exception.UserNotFoundException;
import com.microservices.notification.kafka.appointment.AppointmentNotification;
import com.microservices.notification.user.UserClient;
import com.microservices.notification.user.UserResponse;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    // private final SpringTemplateEngine templateEngine;
    private final UserClient userClient;

    @Async
    public void sendSuccessfulAppointmentConfirmation(AppointmentNotification notification) {

        UserResponse patient = userClient.findById(notification.patientId()).getBody();
        if (patient == null) {
            throw new UserNotFoundException("Patient not found with ID: " + notification.patientId());
        }

        UserResponse doctor = userClient.findById(notification.doctorId()).getBody();
        if (doctor == null) {
            throw new UserNotFoundException("Doctor not found with ID: " + notification.doctorId());
        }

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, MimeMessageHelper.MULTIPART_MODE_NO, StandardCharsets.UTF_8.name());

            messageHelper.setFrom("anyen@gmail.com");
            messageHelper.setTo(patient.email());
            messageHelper.setSubject("Xác nhận đặt lịch khám thành công");

            String formattedDate = notification.appointmentDate()
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            String formattedTime = notification.appointmentTime()
                    .format(DateTimeFormatter.ofPattern("HH:mm"));

            String textContent = String.format("""
        Chào bạn,

        Bạn đã đặt lịch khám bệnh trầm cảm thành công với thông tin sau:

        🩺 Bác sĩ: Dr. %s
        📅 Ngày khám: %s
        🕒 Giờ khám: %s
        🔖 Mã lịch hẹn: #%d
        📌 Trạng thái: %s

        Nếu bạn có bất kỳ câu hỏi nào, vui lòng liên hệ với chúng tôi qua email này.

        Chúc bạn luôn mạnh khỏe và an yên.

        Trân trọng,
        An Yên.
    """,
                    doctor.fullName(),
                    formattedDate,
                    formattedTime,
                    notification.id(),
                    notification.status());

            messageHelper.setText(textContent, false);
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            log.error("Error creating MimeMessage: {}", e.getMessage());
            return;
        }
    }

}
