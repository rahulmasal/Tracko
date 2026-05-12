package com.tracko.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveApproveRequest {
    @NotNull(message = "Leave request ID is required")
    private Long leaveRequestId;

    @NotBlank(message = "Action is required")
    private String action;

    private String comments;
}
