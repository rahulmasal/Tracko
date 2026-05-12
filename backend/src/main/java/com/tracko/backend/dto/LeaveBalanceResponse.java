package com.tracko.backend.dto;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveBalanceResponse {
    private Long userId;
    private String userName;
    private Integer year;
    private List<LeaveTypeBalance> balances;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LeaveTypeBalance {
        private String type;
        private Double totalAllocated;
        private Double used;
        private Double pending;
        private Double remaining;
        private Double carriedForward;
    }
}
