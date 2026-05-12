package com.tracko.backend.controller;

import com.tracko.backend.dto.*;
import com.tracko.backend.security.CustomUserDetails;
import com.tracko.backend.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/employee")
    public ResponseEntity<ApiResponse<DashboardEmployeeResponse>> getEmployeeDashboard(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        DashboardEmployeeResponse dashboard = dashboardService.getEmployeeDashboard(userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success(dashboard));
    }

    @GetMapping("/manager")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<DashboardManagerResponse>> getManagerDashboard(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        DashboardManagerResponse dashboard = dashboardService.getManagerDashboard(userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success(dashboard));
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<DashboardAdminResponse>> getAdminDashboard() {
        DashboardAdminResponse dashboard = dashboardService.getAdminDashboard();
        return ResponseEntity.ok(ApiResponse.success(dashboard));
    }
}
