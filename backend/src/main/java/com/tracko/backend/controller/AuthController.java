package com.tracko.backend.controller;

import com.tracko.backend.dto.*;
import com.tracko.backend.security.CustomUserDetails;
import com.tracko.backend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    @PostMapping("/login-otp")
    public ResponseEntity<ApiResponse<String>> loginOtp(@Valid @RequestBody OtpRequest request) {
        String otp = authService.generateOtp(request.getMobile());
        return ResponseEntity.ok(ApiResponse.success("OTP sent successfully"));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<LoginResponse>> verifyOtp(@Valid @RequestBody OtpVerifyRequest request) {
        LoginResponse response = authService.loginWithOtp(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<LoginResponse>> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        LoginResponse response = authService.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.success("Token refreshed", response));
    }

    @PostMapping("/biometric/register")
    public ResponseEntity<ApiResponse<LoginResponse>> registerBiometric(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody String biometricKey) {
        LoginResponse response = authService.registerBiometric(userDetails.getUserId(), biometricKey);
        return ResponseEntity.ok(ApiResponse.success("Biometric registered", response));
    }

    @PostMapping("/biometric/login")
    public ResponseEntity<ApiResponse<LoginResponse>> biometricLogin(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    @PostMapping("/password/reset/request")
    public ResponseEntity<ApiResponse<Void>> requestPasswordReset(@RequestParam String email) {
        authService.requestPasswordReset(email);
        return ResponseEntity.ok(ApiResponse.success("Password reset email sent"));
    }

    @PostMapping("/password/reset")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@RequestParam String email,
                                                            @RequestParam String token,
                                                            @RequestParam String newPassword) {
        authService.resetPassword(email, token, newPassword);
        return ResponseEntity.ok(ApiResponse.success("Password reset successful"));
    }

    @PostMapping("/device/bind")
    public ResponseEntity<ApiResponse<Void>> bindDevice(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam String deviceId) {
        authService.bindDevice(userDetails.getUserId(), deviceId);
        return ResponseEntity.ok(ApiResponse.success("Device bound successfully"));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout() {
        return ResponseEntity.ok(ApiResponse.success("Logged out successfully"));
    }
}
