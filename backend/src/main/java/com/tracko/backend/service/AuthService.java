package com.tracko.backend.service;

import com.tracko.backend.dto.LoginRequest;
import com.tracko.backend.dto.LoginResponse;
import com.tracko.backend.dto.OtpVerifyRequest;
import com.tracko.backend.exception.BusinessException;
import com.tracko.backend.exception.ResourceNotFoundException;
import com.tracko.backend.exception.UnauthorizedException;
import com.tracko.backend.model.User;
import com.tracko.backend.repository.UserRepository;
import com.tracko.backend.security.CustomUserDetails;
import com.tracko.backend.security.JwtTokenProvider;
import com.tracko.backend.security.TokenBlacklistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenBlacklistService tokenBlacklistService;

    @Value("${app.security.max-login-attempts:5}")
    private int maxLoginAttempts;

    @Transactional
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getUsername())
            .orElseGet(() -> userRepository.findByMobile(request.getUsername())
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials")));

        if (Boolean.TRUE.equals(user.getIsLocked())) {
            throw new BusinessException("Account is locked. Please contact administrator.");
        }

        if (!Boolean.TRUE.equals(user.getIsActive())) {
            throw new BusinessException("Account is deactivated. Please contact administrator.");
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
            user.setFailedAttempts(0);
            user.setLastLoginAt(LocalDateTime.now());
            user.setLastLoginIp(request.getDeviceId());
            userRepository.save(user);

            return buildLoginResponse(authentication, user);
        } catch (BadCredentialsException e) {
            user.setFailedAttempts(user.getFailedAttempts() != null ? user.getFailedAttempts() + 1 : 1);
            if (user.getFailedAttempts() >= maxLoginAttempts) {
                user.setIsLocked(true);
            }
            userRepository.save(user);
            throw new UnauthorizedException("Invalid credentials");
        }
    }

    public LoginResponse loginWithOtp(OtpVerifyRequest request) {
        User user = userRepository.findByMobile(request.getMobile())
            .orElseThrow(() -> new UnauthorizedException("User not found"));

        if (Boolean.TRUE.equals(user.getIsLocked())) {
            throw new BusinessException("Account is locked");
        }

        if (!verifyOtp(user, request.getOtp())) {
            throw new UnauthorizedException("Invalid OTP");
        }

        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        CustomUserDetails userDetails = new CustomUserDetails(user);
        String accessToken = jwtTokenProvider.generateToken(
            user.getId(), user.getEmail(), getRoleNames(user));
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId(), user.getEmail());

        return buildTokenResponse(accessToken, refreshToken, user);
    }

    public LoginResponse refreshToken(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new UnauthorizedException("Invalid refresh token");
        }

        Long userId = jwtTokenProvider.getUserIdFromToken(refreshToken);
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        String newAccessToken = jwtTokenProvider.generateToken(
            user.getId(), user.getEmail(), getRoleNames(user));
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(user.getId(), user.getEmail());

        return buildTokenResponse(newAccessToken, newRefreshToken, user);
    }

    public LoginResponse registerBiometric(Long userId, String biometricKey) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        user.setOtpSecret(biometricKey);
        userRepository.save(user);
        CustomUserDetails userDetails = new CustomUserDetails(user);
        String accessToken = jwtTokenProvider.generateToken(
            user.getId(), user.getEmail(), getRoleNames(user));
        return buildTokenResponse(accessToken, null, user);
    }

    @Transactional
    public void requestPasswordReset(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        String resetToken = generateResetToken();
        user.setOtpSecret(passwordEncoder.encode(resetToken));
        userRepository.save(user);
        log.info("Password reset token for {}: {}", email, resetToken);
    }

    @Transactional
    public void resetPassword(String email, String token, String newPassword) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        if (user.getOtpSecret() == null || !passwordEncoder.matches(token, user.getOtpSecret())) {
            throw new BusinessException("Invalid reset token");
        }
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setOtpSecret(null);
        user.setFailedAttempts(0);
        user.setIsLocked(false);
        userRepository.save(user);
    }

    /**
     * Logout by blacklisting the current JWT token.
     */
    public void logout(String token) {
        try {
            long expirationMs = jwtTokenProvider.getExpirationMs();
            tokenBlacklistService.blacklist(token, expirationMs);
            log.info("Token blacklisted on logout");
        } catch (Exception e) {
            log.error("Failed to blacklist token on logout: {}", e.getMessage());
        }
    }

    @Transactional
    public void bindDevice(Long userId, String deviceId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        user.setDeviceBindingId(deviceId);
        userRepository.save(user);
    }

    public String generateOtp(String mobile) {
        User user = userRepository.findByMobile(mobile)
            .orElseThrow(() -> new ResourceNotFoundException("User", "mobile", mobile));
        String otp = String.format("%06d", new SecureRandom().nextInt(999999));
        user.setOtpSecret(passwordEncoder.encode(otp));
        userRepository.save(user);
        log.info("OTP for {}: {}", mobile, otp);
        return otp;
    }

    private boolean verifyOtp(User user, String otp) {
        if (user.getOtpSecret() == null) return false;
        return passwordEncoder.matches(otp, user.getOtpSecret());
    }

    private String generateResetToken() {
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private LoginResponse buildLoginResponse(Authentication authentication, User user) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        List<String> roleNames = getRoleNames(user);
        String accessToken = jwtTokenProvider.generateToken(
            user.getId(), user.getEmail(), roleNames);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId(), user.getEmail());
        return buildTokenResponse(accessToken, refreshToken, user);
    }

    private LoginResponse buildTokenResponse(String accessToken, String refreshToken, User user) {
        List<String> roleNames = getRoleNames(user);
        List<String> permissions = user.getRoles().stream()
            .flatMap(role -> role.getPermissions().stream())
            .map(permission -> permission.getCode())
            .collect(Collectors.toList());

        LoginResponse.UserInfo userInfo = LoginResponse.UserInfo.builder()
            .id(user.getId())
            .employeeCode(user.getEmployeeCode())
            .fullName(user.getFullName())
            .email(user.getEmail())
            .mobile(user.getMobile())
            .designation(user.getDesignation())
            .profilePhotoUrl(user.getProfilePhotoUrl())
            .departmentId(user.getDepartmentId())
            .branchId(user.getBranchId())
            .managerId(user.getManagerId())
            .shiftId(user.getShiftId())
            .roles(roleNames)
            .permissions(permissions)
            .build();

        return LoginResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .tokenType("Bearer")
            .expiresIn(jwtTokenProvider.getExpirationMs())
            .user(userInfo)
            .build();
    }

    private List<String> getRoleNames(User user) {
        return user.getRoles().stream()
            .map(role -> role.getName())
            .collect(Collectors.toList());
    }
}
