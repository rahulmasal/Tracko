package com.tracko.backend.notification;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;

@Slf4j
@Service
public class FcmPushService {

    @Value("${app.notification.fcm.enabled:true}")
    private boolean enabled;

    @Value("${app.notification.fcm.credentials-path:}")
    private String credentialsPath;

    @PostConstruct
    public void initialize() {
        if (enabled && credentialsPath != null && !credentialsPath.isEmpty()) {
            try {
                FileInputStream serviceAccount = new FileInputStream(credentialsPath);
                FirebaseApp.initializeApp();
                log.info("Firebase app initialized");
            } catch (Exception e) {
                log.warn("Failed to initialize Firebase: {}", e.getMessage());
            }
        }
    }

    public void sendPush(String deviceToken, String title, String body, String imageUrl) {
        if (!enabled || deviceToken == null) {
            log.debug("Push notification skipped (disabled or no token)");
            return;
        }

        try {
            Notification notification = Notification.builder()
                .setTitle(title)
                .setBody(body)
                .setImage(imageUrl)
                .build();

            Message message = Message.builder()
                .setToken(deviceToken)
                .setNotification(notification)
                .build();

            String response = FirebaseMessaging.getInstance().send(message);
            log.info("Push notification sent: {}", response);
        } catch (Exception e) {
            log.error("Failed to send push notification: {}", e.getMessage());
            if (e.getMessage() != null && e.getMessage().contains("UNREGISTERED")) {
                log.warn("Device token expired for token: {}", deviceToken);
            }
        }
    }

    public void sendDataMessage(String deviceToken, String title, String body, java.util.Map<String, String> data) {
        if (!enabled || deviceToken == null) return;

        try {
            com.google.firebase.messaging.Notification notification = Notification.builder()
                .setTitle(title).setBody(body).build();

            Message.Builder builder = Message.builder()
                .setToken(deviceToken)
                .setNotification(notification);

            if (data != null) {
                builder.putAllData(data);
            }

            String response = FirebaseMessaging.getInstance().send(builder.build());
            log.info("Data message sent: {}", response);
        } catch (Exception e) {
            log.error("Failed to send data message: {}", e.getMessage());
        }
    }
}
