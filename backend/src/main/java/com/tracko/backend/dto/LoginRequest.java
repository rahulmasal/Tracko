package com.tracko.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {
    @NotBlank(message = "Email or mobile is required")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;

    private String deviceId;
    private String fcmToken;
}
