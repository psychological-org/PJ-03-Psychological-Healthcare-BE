package com.microservices.notification.kafka;

import com.microservices.notification.email.EmailService;
import com.microservices.notification.exception.NotificationNotFoundException;
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
import com.microservices.notification.post.PostResponse;
import com.microservices.notification.user.UserClient;
import com.microservices.notification.user.UserResponse;
import com.microservices.notification.user_notification.UserNotificationRequest;
import com.microservices.notification.user_notification.UserNotificationService;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
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
        public void commentPostNotification(CommentNotification commentNotification,  @Header(value = "Authorization", required = false) String jwt) throws MessagingException {
                log.info("Consuming the message from comment-topic Topic:: {}", commentNotification);
                try {
                        final String templateName = " đã bình luận về bài viết của bạn";

                        try {
                                UserResponse userResponse = userClient.findById("682a976ae5cb142bd93c585e").getBody();
                                log.info("User found: {}", userResponse);
                        } catch (FeignException e) {
                                log.error("User not found for ID: {}", e);
                                return;
                        }

                        NotificationResponse notificationResponse;
                        try {
                                // Thử lấy template, nếu không tìm thấy thì ném NotificationNotFoundException
                                notificationResponse = notificationService.findNotificationByName(templateName);
                        } catch (NotificationNotFoundException notFound) {
                                NotificationRequest newTemplate = new NotificationRequest(
                                        null,
                                        templateName,
                                        NotificationType.COMMENT_NOTIFICATION
                                );
                                String newId = notificationService.createNotification(newTemplate);
                                notificationResponse = notificationService.findOneById(newId);
                        }
                        System.out.println("notificationResponse = " + notificationResponse);

                        PostResponse postResponse;
                        try {
                                postResponse = postClient.findById(commentNotification.postId()).getBody();
                        } catch (Exception e) {
                                log.error("Post not found for ID: {}", e);
                                return;
                        }

                        log.info("Created new notification template: {}", notificationResponse);

                        String userNotificationContent = "Người dùng " + commentNotification.userId()
                                        + notificationResponse.content();
                        // Lấy id user của post
                        UserNotificationRequest userNotificationRequest = new UserNotificationRequest(null,
                                        postResponse.userId(), notificationResponse.id(), userNotificationContent,
                                        false);
                        userNotificationService.createUserNotification(userNotificationRequest);

                        log.info("Save comment notification: {}", notificationResponse);

                        // send push notification to client
                        messagingTemplate.convertAndSendToUser(
                                        commentNotification.userId(),
                                        "/notification/comment",
                                        notificationResponse);

                } catch (RuntimeException e) {
                        log.error("Error sending notification when someone comments: {}", e);
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

                        NotificationRequest newNotification = new NotificationRequest(null,
                                        " đã theo dõi bạn trên nền tảng", NotificationType.FOLLOW_NOTIFICATION);
                        String notificationResponse = notificationService.createNotification(newNotification);

                        String userNotificationContent = "Người dùng " + followUser.fullName()
                                        + newNotification.content();
                        UserNotificationRequest userNotificationRequest = new UserNotificationRequest(null,
                                        followNotification.receiverId(), newNotification.id(), userNotificationContent,
                                        false);
                        userNotificationService.createUserNotification(userNotificationRequest);

                        NotificationResponse notification = notificationService.findOneById(notificationResponse);
                        log.info("Save follow notification: {}", notification);

                        // send push notification to client
                        messagingTemplate.convertAndSendToUser(
                                        followNotification.receiverId(),
                                        "/notification/follow",
                                        notification);

                        messagingTemplate.convertAndSendToUser(followNotification.receiverId(), "/notification/follow",
                                        notificationResponse);

                } catch (RuntimeException e) {
                        log.error("Error sending notification when someone follows: {}", e.getMessage());
                }
        }

        @KafkaListener(topics = "appointment-topic", groupId = "appointmentGroup")
        public void consumerAppointmentNotification(AppointmentNotification notification) throws MessagingException {
                log.info("Consuming the message from appointment-topic Topic:: {}", notification.toString());
                try {
                        NotificationRequest request = new NotificationRequest(null, "Thông báo xác nhận đặt lịch",
                                        NotificationType.APPOINTMENT_NOTIFICATION);
                        String notificationResponse = notificationService.createNotification(request);

                        UserNotificationRequest userNotificationRequest = new UserNotificationRequest(null,
                                        notification.patientId(), notificationResponse, "Thông báo xác nhận đặt lịch",
                                        false);
                        userNotificationService.createUserNotification(userNotificationRequest);

                        emailService.sendSuccessfulAppointmentConfirmation(notification);
                } catch (RuntimeException e) {
                        log.error("Error sending appointment confirmation email: {}", e.getMessage());
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
                        NotificationRequest newNotification = new NotificationRequest(
                                        null,
                                        "Người dùng đăng tải bài viết mới",
                                        NotificationType.POST_NOTIFICATION);
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
                                                false);

                                userNotificationService.createUserNotification(userNotificationRequest);

                                messagingTemplate.convertAndSendToUser(
                                                targetUserId,
                                                "/notification/post",
                                                notification);
                        }
                } catch (Exception e) {
                        log.error("Error processing post notification: {}", e.getMessage());
                }
        }

}
