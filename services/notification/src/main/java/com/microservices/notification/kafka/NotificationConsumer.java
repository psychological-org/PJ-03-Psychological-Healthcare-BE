package com.microservices.notification.kafka;
import com.microservices.notification.email.EmailService;
import com.microservices.notification.follow.FollowClient;
import com.microservices.notification.follow.FollowResponse;
import com.microservices.notification.kafka.appointment.AppointmentNotification;
import com.microservices.notification.kafka.comment.CommentNotification;
import com.microservices.notification.kafka.follow.FollowNotification;
import com.microservices.notification.kafka.post.PostNotification;
import com.microservices.notification.notification.NotificationRequest;
import com.microservices.notification.notification.NotificationResponse;
import com.microservices.notification.notification.NotificationService;
import com.microservices.notification.notification.NotificationType;
import com.microservices.notification.post.PostClient;
import com.microservices.notification.user.UserClient;
import com.microservices.notification.user.UserResponse;
import com.microservices.notification.user_notification.UserNotificationRequest;
import com.microservices.notification.user_notification.UserNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationConsumer {
        private final EmailService emailService;
        private final NotificationService notificationService;
        private final UserNotificationService userNotificationService;
        private final FollowClient followClient;
        private final SimpMessagingTemplate messagingTemplate;

        @Autowired
        private final UserClient userClient;
        private final PostClient postClient;

        @KafkaListener(topics = "comment-topic", groupId = "commentGroup")
        public void commentPostNotification(CommentNotification commentNotification) throws MessagingException {
                log.info("Consuming the message from comment-topic Topic:: {}", commentNotification);
                UserResponse commentUser = userClient.findById(commentNotification.userId()).getBody();
                try {
                        NotificationRequest newNotification = new NotificationRequest(null, " đã bình luận về bài viết của bạn", NotificationType.COMMENT_NOTIFICATION);
                        String notificationResponse = notificationService.createNotification(newNotification);

                        String userNotificationContent = "Người dùng " + commentUser.fullName() + newNotification.content();
                        UserNotificationRequest userNotificationRequest = new UserNotificationRequest(null, commentNotification.userId(), newNotification.id(), userNotificationContent, false);
                        String userNotification = userNotificationService.createUserNotification(userNotificationRequest);

                        NotificationResponse notification = notificationService.findOneById(notificationResponse);
                        log.info("Save comment notification: {}", notification);
                        // send push notification to client
                        messagingTemplate.convertAndSendToUser(
                                commentNotification.userId(),
                                "/notification/comment",
                                notification
                        );

                } catch (RuntimeException e) {
                        log.error("Error sending notification when someone comments: {}", e.getMessage());
                }
        }

        @KafkaListener(topics = "follow-topic", groupId = "followGroup")
        public void consumerFollowNotification(FollowNotification followNotification) throws MessagingException {
                log.info("Consuming the message from follow-topic Topic:: {}", followNotification);
                UserResponse followUser = userClient.findById(followNotification.senderId()).getBody();
                try {
                        NotificationRequest newNotification = new NotificationRequest(null, " đã theo dõi bạn trên nền tảng", NotificationType.FOLLOW_NOTIFICATION);
                        String notificationResponse = notificationService.createNotification(newNotification);

                        String userNotificationContent = "Người dùng " + followUser.fullName() + newNotification.content();
                        UserNotificationRequest userNotificationRequest = new UserNotificationRequest(null, followNotification.receiverId(), newNotification.id(), userNotificationContent, false);
                        String userNotification = userNotificationService.createUserNotification(userNotificationRequest);

                        NotificationResponse notification = notificationService.findOneById(notificationResponse);
                        log.info("Save follow notification: {}", notification);
                        // send push notification to client
                        messagingTemplate.convertAndSendToUser(
                                followNotification.receiverId(),
                                "/notification/follow",
                                notification
                        );

                        messagingTemplate.convertAndSendToUser(followNotification.receiverId(), "/notification/follow", notificationResponse);
                } catch (RuntimeException e) {
                        log.error("Error sending notification when someone follows: {}", e.getMessage());
                }
        }

        @KafkaListener(topics = "appointment-topic", groupId = "appointmentGroup")
        public void consumerAppointmentNotification(AppointmentNotification notification) throws MessagingException {
                log.info("Consuming the message from appointment-topic Topic:: {}", notification.toString());
                try {
                        NotificationRequest request = new NotificationRequest(null, "Thông báo xác nhận đặt lịch", NotificationType.APPOINTMENT_NOTIFICATION);
                        String notificationResponse = notificationService.createNotification(request);

                        UserNotificationRequest userNotificationRequest = new UserNotificationRequest(null, notification.patientId(), notificationResponse, "Thông báo xác nhận đặt lịch", false);
                        String userNotification = userNotificationService.createUserNotification(userNotificationRequest);

                        emailService.sendSuccessfulAppointmentConfirmation(notification);
                } catch (RuntimeException e) {
                        log.error("Error sending appointment confirmation email: {}", e.getMessage());
                }
        }

        @KafkaListener(topics = "post-topic", groupId = "postGroup")
        public void consumerCreateNewPostNotification(PostNotification post) throws MessagingException {
                log.info("Consuming the message from post-topic Topic:: {}", post);
                UserResponse poster = userClient.findById(post.userId()).getBody();
                String userResponseContent = "Người dùng " + poster.fullName() + " đã đăng tải bài viết mới";
                try {
                        NotificationRequest newNotification = new NotificationRequest(
                                null,
                                "Người dùng đăng tải bài viết mới",
                                NotificationType.POST_NOTIFICATION
                        );
                        String notificationId = notificationService.createNotification(newNotification);
                        NotificationResponse notification = notificationService.findOneById(notificationId);

                        List<FollowResponse> followResponse = followClient
                                .findAllFriendByUserIdNotPaginate(post.userId())
                                .getBody();

                        log.info("Save follower: {}", followResponse);

                        if (followResponse == null || followResponse.isEmpty()) {
                                log.info("No followers found for user: {}", post.userId());
                                return;
                        }

                        for (FollowResponse follow : followResponse) {
                                String targetUserId = follow.senderId().equals(post.userId())
                                        ? follow.receiverId()
                                        : follow.senderId();

                                UserNotificationRequest userNotificationRequest = new UserNotificationRequest(
                                        null,
                                        targetUserId,
                                        notificationId,
                                        userResponseContent,
                                        false
                                );

                                userNotificationService.createUserNotification(userNotificationRequest);

                                messagingTemplate.convertAndSendToUser(
                                        targetUserId,
                                        "/notification/post",
                                        notification
                                );
                        }
                } catch (Exception e) {
                        log.error("Error processing post notification: {}", e.getMessage());
                }
        }


}
