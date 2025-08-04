package com.microservices.notification.kafka;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.microservices.notification.email.EmailService;
import com.microservices.notification.exception.NotificationNotFoundException;
import com.microservices.notification.fcm_token.FcmTokenClient;
import com.microservices.notification.fcm_token.FcmTokenResponse;
import com.microservices.notification.firebase.FcmPushService;
import com.microservices.notification.follow.FollowClient;
import com.microservices.notification.follow.FollowResponse;
import com.microservices.notification.jwt.JwtService;
import com.microservices.notification.kafka.appointment.AppointmentNotification;
import com.microservices.notification.kafka.comment.CommentNotification;
import com.microservices.notification.kafka.follow.FollowNotification;
import com.microservices.notification.kafka.post.PostNotification;
import com.microservices.notification.notification.NotificationRequest;
import com.microservices.notification.notification.NotificationResponse;
import com.microservices.notification.notification.NotificationService;
import com.microservices.notification.notification.NotificationType;
import com.microservices.notification.participant_community.ParticipantCommunityClient;
import com.microservices.notification.participant_community.ParticipantCommunityResponse;
import com.microservices.notification.post.PostClient;
import com.microservices.notification.post.PostResponse;
import com.microservices.notification.user.UserClient;
import com.microservices.notification.user.UserResponse;
import com.microservices.notification.user_notification.UserNotification;
import com.microservices.notification.user_notification.UserNotificationRequest;
import com.microservices.notification.user_notification.UserNotificationService;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationConsumer {
        private final EmailService emailService;
        private final NotificationService notificationService;
        private final UserNotificationService userNotificationService;
        private final ParticipantCommunityClient participantCommunityResponse;

        private final UserClient userClient;
        private final PostClient postClient;
        private final FcmTokenClient fcmTokenClient;
        private final JwtService jwtService;
        private final FcmPushService fcmPushService;
        // private final PostClient postClient;

        @KafkaListener(topics = "comment-topic", groupId = "commentGroup")
        public void commentPostNotification(CommentNotification commentNotification, @Header(value = "Authorization", required = false) String jwt) throws MessagingException {
                log.info("Consuming the message from comment-topic Topic:: {}", commentNotification);
                try {
                        final String templateName = " đã bình luận về bài viết của bạn";
                        UserResponse userResponse;
                        try {
                                userResponse = userClient.findById(commentNotification.userId()).getBody();
                        } catch (FeignException e) {
                                log.error("User not found for ID: {}", commentNotification.userId());
                                return;
                        }

                        NotificationResponse notificationResponse;
                        try {
                                notificationResponse = notificationService.findNotificationByName(templateName);
                        } catch (NotificationNotFoundException notFound) {
                                NotificationRequest newTemplate = new NotificationRequest(null, templateName, NotificationType.COMMENT_NOTIFICATION);
                                String newId = notificationService.createNotification(newTemplate);
                                notificationResponse = notificationService.findOneById(newId);
                        }

                        PostResponse postResponse;
                        try {
                                postResponse = postClient.findById(commentNotification.postId()).getBody();
                        } catch (Exception e) {
                                log.error("Post not found for ID: {}", commentNotification.postId());
                                return;
                        }

                        String userNotificationContent = userResponse.fullName() + notificationResponse.content();
                        UserNotificationRequest userNotificationRequest = new UserNotificationRequest(
                                null, postResponse.userId(), notificationResponse.id(), userNotificationContent, false);
                        String userNotificationId = userNotificationService.createUserNotification(userNotificationRequest);
                        log.info("Created user notification with ID: {}", userNotificationId);

                        List<FcmTokenResponse> fcmTokenResponses = fcmTokenClient.findByUserId(postResponse.userId()).getBody();
                        if (fcmTokenResponses != null && !fcmTokenResponses.isEmpty()) {
                                fcmTokenResponses.forEach(fcmTokenResponse -> {
                                        try {
                                                fcmPushService.sendToToken(
                                                        fcmTokenResponse.fcmToken(),
                                                        "Bình luận mới",
                                                        userNotificationContent,
                                                        null,
                                                        userNotificationId,
                                                        "patient"
                                                );
                                        } catch (FirebaseMessagingException e) {
                                                log.error("Error sending push notification: {}", e.getMessage());
                                        }
                                });
                        }
                } catch (RuntimeException e) {
                        log.error("Error sending notification when someone comments: {}", e.getMessage());
                }
        }

        @KafkaListener(topics = "follow-topic", groupId = "followGroup")
        public void consumerFollowNotification(FollowNotification followNotification) throws MessagingException {
                log.info("Consuming the message from follow-topic Topic:: {}", followNotification);
                UserResponse followUser = null;
                try {
                        followUser = userClient.findById(followNotification.senderId()).getBody();
                        if (followUser == null) {
                                log.error("User not found for follow sender ID: {}", followNotification.senderId());
                                return;
                        }

                        final String templateName = " đã theo dõi bạn trên nền tảng";
                        NotificationResponse newNotification;
                        try {
                                newNotification = notificationService.findNotificationByName(templateName);
                        } catch (NotificationNotFoundException notFound) {
                                NotificationRequest newTemplate = new NotificationRequest(
                                        null,
                                        templateName,
                                        NotificationType.FOLLOW_NOTIFICATION
                                );
                                String newId = notificationService.createNotification(newTemplate);
                                newNotification = notificationService.findOneById(newId);
                        }

                        String userNotificationContent = "Người dùng " + followUser.fullName()
                                        + newNotification.content();
                        UserNotificationRequest userNotificationRequest = new UserNotificationRequest(null,
                                        followNotification.receiverId(), newNotification.id(), userNotificationContent,
                                        false);
                        userNotificationService.createUserNotification(userNotificationRequest);

                        // send push notification to client
                        List<FcmTokenResponse> fcmTokenResponses = fcmTokenClient
                                        .findByUserId(followNotification.receiverId()).getBody();
                        System.out.println("fcmTokenResponses = " + fcmTokenResponses.get(fcmTokenResponses.size() - 1).toString());
                        fcmTokenResponses.forEach(fcmTokenResponse -> {
                                try {
                                        notificationService.sendPushNotificationByFirebase(fcmTokenResponse.fcmToken(),
                                                "Người dùng theo dõi bạn",
                                                userNotificationContent);
                                } catch (Exception e) {
                                        log.error("Error sending push notification: {}", e.getMessage());
                                }
                        });

                } catch (RuntimeException e) {
                        log.error("Error sending notification when someone follows: {}", e.getMessage());
                }
        }

        @KafkaListener(topics = "appointment-topic", groupId = "appointmentGroup")
        public void consumerAppointmentNotification(AppointmentNotification notification,
                                                    @Header(value = "Authorization", required = false) String authorizationHeader) throws MessagingException {
                log.info("Consuming message from appointment-topic: {}", notification.toString());
                try {
                        final String templateName = "Thông báo xác nhận đặt lịch";
                        NotificationResponse notificationResponse;
                        try {
                                notificationResponse = notificationService.findNotificationByName(templateName);
                        } catch (NotificationNotFoundException notFound) {
                                NotificationRequest newTemplate = new NotificationRequest(null, templateName, NotificationType.APPOINTMENT_NOTIFICATION);
                                String newId = notificationService.createNotification(newTemplate);
                                notificationResponse = notificationService.findOneById(newId);
                        }

                        String patientName = "Bệnh nhân không xác định";
                        String patientRole = "patient";
                        try {
                                ResponseEntity<UserResponse> userResponse = userClient.findById(notification.patientId());
                                UserResponse user = userResponse.getBody();
                                if (user != null && user.fullName() != null) {
                                        patientName = user.fullName();
                                        patientRole = user.role() != null ? user.role() : "patient";
                                }
                        } catch (Exception e) {
                                log.error("Failed to fetch patient name for patientId {}: {}", notification.patientId(), e.getMessage());
                        }

                        String updaterRole = "unknown";
                        String updaterId = null;
                        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                                String token = authorizationHeader.replace("Bearer ", "");
                                updaterId = jwtService.extractUserId(token);
                                updaterRole = jwtService.extractRole(token);
                        }

                        record NotificationInfo(String title, String body, String targetUserId, String role) {}
                        List<NotificationInfo> notifications = switch (notification.status()) {
                                case "pending" -> List.of(
                                        new NotificationInfo(
                                                "Lịch hẹn mới",
                                                String.format("Bệnh nhân %s đã đặt lịch vào %s lúc %s. Vui lòng xác nhận hoặc từ chối.",
                                                        patientName, notification.appointmentDate(), notification.appointmentTime()),
                                                notification.doctorId(),
                                                "doctor"
                                        )
                                );
                                case "confirmed" -> List.of(
                                        new NotificationInfo(
                                                "Xác nhận lịch hẹn",
                                                String.format("Bạn đã xác nhận lịch hẹn với bệnh nhân %s vào %s lúc %s.",
                                                        patientName, notification.appointmentDate(), notification.appointmentTime()),
                                                notification.doctorId(),
                                                "doctor"
                                        ),
                                        new NotificationInfo(
                                                "Lịch hẹn của bạn được xác nhận",
                                                String.format("Lịch hẹn của bạn vào %s lúc %s với bác sĩ đã được xác nhận. Vui lòng đến đúng giờ.",
                                                        notification.appointmentDate(), notification.appointmentTime()),
                                                notification.patientId(),
                                                "patient"
                                        )
                                );
                                case "cancelled" -> {
                                        if ("patient".equalsIgnoreCase(updaterRole)) {
                                                yield List.of(
                                                        new NotificationInfo(
                                                                "Bạn đã hủy lịch hẹn",
                                                                String.format("Bạn đã hủy lịch hẹn vào %s lúc %s.",
                                                                        notification.appointmentDate(), notification.appointmentTime()),
                                                                notification.patientId(),
                                                                "patient"
                                                        )
                                                );
                                        } else if ("doctor".equalsIgnoreCase(updaterRole)) {
                                                yield List.of(
                                                        new NotificationInfo(
                                                                "Từ chối lịch hẹn",
                                                                String.format("Bạn đã từ chối lịch hẹn của bệnh nhân %s vào %s lúc %s.",
                                                                        patientName, notification.appointmentDate(), notification.appointmentTime()),
                                                                notification.doctorId(),
                                                                "doctor"
                                                        ),
                                                        new NotificationInfo(
                                                                "Lịch hẹn của bạn bị từ chối",
                                                                String.format("Lịch hẹn của bạn vào %s lúc %s đã bị từ chối. Vui lòng đặt lịch khác.",
                                                                        notification.appointmentDate(), notification.appointmentTime()),
                                                                notification.patientId(),
                                                                "patient"
                                                        )
                                                );
                                        } else {
                                                log.warn("Invalid updaterRole: {}", updaterRole);
                                                yield List.of();
                                        }
                                }
                                default -> {
                                        log.warn("Unknown appointment status: {}", notification.status());
                                        yield List.of();
                                }
                        };

                        for (NotificationInfo info : notifications) {
                                // Tạo UserNotification
                                UserNotificationRequest userNotificationRequest = new UserNotificationRequest(
                                        null, info.targetUserId(), notificationResponse.id(), info.body(), false);
                                String userNotificationId = userNotificationService.createUserNotification(userNotificationRequest);
                                log.info("Created user notification with ID: {}", userNotificationId);

                                ResponseEntity<List<FcmTokenResponse>> response = fcmTokenClient.findByUserId(info.targetUserId());
                                List<FcmTokenResponse> tokens = response.getBody();
                                if (tokens != null && !tokens.isEmpty()) {
                                        for (FcmTokenResponse token : tokens) {
                                                if (token.fcmToken() == null || token.fcmToken().isEmpty()) {
                                                        log.warn("Invalid FCM token for userId {}: token is null or empty", info.targetUserId());
                                                        continue;
                                                }
                                                try {
                                                        String fcmResponse = fcmPushService.sendToToken(
                                                                token.fcmToken(), info.title(), info.body(), notification.id(), userNotificationId, info.role());
                                                        log.info("FCM response for userId {}: {}", info.targetUserId(), fcmResponse);
                                                } catch (FirebaseMessagingException e) {
                                                        log.error("Failed to send FCM notification to userId {}: {}", info.targetUserId(), e.getMessage());
                                                }
                                        }
                                } else {
                                        log.warn("No FCM tokens found for userId: {}, role: {}", info.targetUserId(), info.role());
                                }
                        }

                        if ("pending".equals(notification.status())) {
                                emailService.sendSuccessfulAppointmentConfirmation(notification);
                                log.info("Sent email confirmation for appointment: {}", notification);
                        }
                } catch (RuntimeException e) {
                        log.error("Error processing appointment notification: {}", e.getMessage());
                }
        }

        @KafkaListener(topics = "post-topic", groupId = "postGroup")
        public void consumerCreateNewPostNotification(PostNotification post) throws MessagingException {
                log.info("Consuming the message from post-topic Topic:: {}", post);
                UserResponse poster = userClient.findById(post.userId()).getBody();
                if (poster == null) {
                        log.error("User not found for poster ID: {}", post.userId());
                        return;
                }
                String userResponseContent = "Người dùng " + poster.fullName() + " đã đăng tải bài viết mới";
                try {
                        final String templateName = "Người dùng đăng tải bài viết mới";
                        NotificationResponse newNotification;
                        try {
                                newNotification = notificationService.findNotificationByName(templateName);
                        } catch (NotificationNotFoundException notFound) {
                                NotificationRequest newTemplate = new NotificationRequest(
                                        null, templateName, NotificationType.POST_NOTIFICATION
                                );
                                String newId = notificationService.createNotification(newTemplate);
                                newNotification = notificationService.findOneById(newId);
                        }

                        List<ParticipantCommunityResponse> followResponse = participantCommunityResponse.CommunityIdNotPaginate(post.communityId())
                                .getBody();

                        log.info("Participants in community {}: {}", post.communityId(), followResponse);

                        if (followResponse == null || followResponse.isEmpty()) {
                                log.info("No participants found for community: {}", post.communityId());
                                return;
                        }

                        // Lọc bỏ người tạo bài đăng
                        for (ParticipantCommunityResponse follow : followResponse) {
                                String targetUserId = follow.userId();
                                if (targetUserId.equals(post.userId())) {
                                        log.info("Skipping notification for post creator: userId={}", targetUserId);
                                        continue;
                                }

                                UserNotificationRequest userNotificationRequest = new UserNotificationRequest(
                                        null, targetUserId, newNotification.id(), userResponseContent, false);
                                String userNotificationId = userNotificationService.createUserNotification(userNotificationRequest);
                                log.info("Created user notification with ID: {} for user: {}", userNotificationId, targetUserId);

                                List<FcmTokenResponse> fcmTokenResponses = fcmTokenClient.findByUserId(targetUserId).getBody();
                                if (fcmTokenResponses != null && !fcmTokenResponses.isEmpty()) {
                                        fcmTokenResponses.forEach(fcmTokenResponse -> {
                                                try {
                                                        fcmPushService.sendToToken(
                                                                fcmTokenResponse.fcmToken(),
                                                                "Bài viết mới",
                                                                userResponseContent,
                                                                null,
                                                                userNotificationId,
                                                                "patient"
                                                        );
                                                        log.info("Sent push notification to user: {}", targetUserId);
                                                } catch (FirebaseMessagingException e) {
                                                        log.error("Error sending push notification to user {}: {}", targetUserId, e.getMessage());
                                                }
                                        });
                                } else {
                                        log.warn("No FCM tokens found for user: {}", targetUserId);
                                }
                        }
                } catch (Exception e) {
                        log.error("Error processing post notification: {}", e.getMessage());
                }
        }

}
