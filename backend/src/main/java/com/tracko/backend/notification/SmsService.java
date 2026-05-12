package com.tracko.backend.notification;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SmsService {

    @Value("${app.notification.twilio.enabled:false}")
    private boolean enabled;

    @Value("${app.notification.twilio.account-sid:}")
    private String accountSid;

    @Value("${app.notification.twilio.auth-token:}")
    private String authToken;

    @Value("${app.notification.twilio.from-number:}")
    private String fromNumber;

    @PostConstruct
    public void initialize() {
        if (enabled && accountSid != null && !accountSid.isEmpty()) {
            Twilio.init(accountSid, authToken);
            log.info("Twilio SMS service initialized");
        }
    }

    public void sendSms(String to, String messageBody) {
        if (!enabled) {
            log.debug("SMS sending is disabled");
            return;
        }

        try {
            Message message = Message.creator(
                new PhoneNumber(to),
                new PhoneNumber(fromNumber),
                messageBody
            ).create();

            log.info("SMS sent to {}: sid={}", to, message.getSid());
        } catch (Exception e) {
            log.error("Failed to send SMS to {}: {}", to, e.getMessage());
        }
    }
}
