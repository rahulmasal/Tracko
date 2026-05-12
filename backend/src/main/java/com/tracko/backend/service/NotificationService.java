package com.tracko.backend.service;

import com.tracko.backend.model.Notification;
import com.tracko.backend.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public Page<Notification> getNotifications(Long userId, Pageable pageable) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    public long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndIsRead(userId, false);
    }

    @Transactional
    public void markAsRead(Long id, Long userId) {
        Notification notification = notificationRepository.findById(id).orElse(null);
        if (notification != null && notification.getUserId().equals(userId)) {
            notification.setIsRead(true);
            notification.setReadAt(LocalDateTime.now());
            notificationRepository.save(notification);
        }
    }

    @Transactional
    public void markAllAsRead(Long userId) {
        notificationRepository.markAllAsRead(userId, LocalDateTime.now());
    }

    @Async
    public void sendNotification(Long userId, String type, String title, String body,
                                  String referenceType, Long referenceId,
                                  String channel, Map<String, String> data) {
        try {
            Notification notification = Notification.builder()
                .userId(userId)
                .type(type)
                .title(title)
                .body(body)
                .referenceType(referenceType)
                .referenceId(referenceId)
                .channel(channel)
                .deliveryStatus("PENDING")
                .build();
            notificationRepository.save(notification);
            log.info("Notification queued for user {}: {}", userId, title);
        } catch (Exception e) {
            log.error("Failed to create notification: {}", e.getMessage());
        }
    }

    @Async
    public void sendBulkNotification(Map<Long, String> recipients, String type, String title,
                                      String body, String referenceType, Long referenceId) {
        for (Map.Entry<Long, String> entry : recipients.entrySet()) {
            sendNotification(entry.getKey(), type, title, body, referenceType, referenceId, "IN_APP", null);
        }
    }
}
