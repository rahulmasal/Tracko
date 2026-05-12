package com.tracko.backend.dto;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    private long expiresIn;
    private UserInfo user;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserInfo {
        private Long id;
        private String employeeCode;
        private String fullName;
        private String email;
        private String mobile;
        private String designation;
        private String profilePhotoUrl;
        private Long departmentId;
        private Long branchId;
        private Long managerId;
        private Long shiftId;
        private List<String> roles;
        private List<String> permissions;
    }
}
