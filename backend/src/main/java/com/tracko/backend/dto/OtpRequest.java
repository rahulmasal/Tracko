package com.tracko.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OtpRequest {
    @NotBlank(message = "Mobile number is required")
    private String mobile;

    private String deviceId;
}
