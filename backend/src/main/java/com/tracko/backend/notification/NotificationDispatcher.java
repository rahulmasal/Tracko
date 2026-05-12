package com.tracko.backend.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationDispatcher {

    private final FcmPushService fcmPushService;
    private final SmsService smsService;
    private final EmailService emailService;

    public void dispatch(String channel, String recipient, String title, String body,
                         Map<String, String> data) {
        switch (channel.toUpperCase()) {
            case "PUSH" -> {
                fcmPushService.sendPush(recipient, title, body, null);
                if (data != null) {
                    fcmPushService.sendDataMessage(recipient, title, body, data);
                }
            }
            case "SMS" -> smsService.sendSms(recipient, body);
            case "EMAIL" -> emailService.sendSimpleEmail(recipient, title, body);
            default -> log.warn("Unknown notification channel: {}", channel);
        }
    }

    public void dispatchAll(String pushToken, String phone, String email,
                             String title, String body, Map<String, String> data) {
        if (pushToken != null) {
            fcmPushService.sendPush(pushToken, title, body, null);
            if (data != null) {
                fcmPushService.sendDataMessage(pushToken, title, body, data);
            }
        }
        if (phone != null) {
            smsService.sendSms(phone, body);
        }
        if (email != null) {
            emailService.sendSimpleEmail(email, title, body);
        }
    }
}
