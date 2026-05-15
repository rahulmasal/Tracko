package com.tracko.backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

    private final StringRedisTemplate redisTemplate;
    private static final int MAX_REQUESTS_PER_MINUTE = 60;
    private static final int MAX_LOGIN_ATTEMPTS_PER_MINUTE = 10;
    private static final String RATE_LIMIT_PREFIX = "ratelimit:";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String clientIp = getClientIp(request);
        String requestUri = request.getRequestURI();

        int maxRequests = requestUri.contains("/auth/login") ? MAX_LOGIN_ATTEMPTS_PER_MINUTE : MAX_REQUESTS_PER_MINUTE;
        String key = RATE_LIMIT_PREFIX + clientIp + ":" + (requestUri.contains("/auth/login") ? "login" : "api");

        try {
            Long count = redisTemplate.opsForValue().increment(key);
            if (count != null && count == 1) {
                redisTemplate.expire(key, 1, TimeUnit.MINUTES);
            }
            if (count != null && count > maxRequests) {
                log.warn("Rate limit exceeded for IP: {} on {}", clientIp, requestUri);
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.setContentType("application/json");
                response.getWriter().write("{\"success\":false,\"message\":\"Too many requests. Please try again later.\"}");
                return;
            }
        } catch (Exception e) {
            log.error("Rate limit check failed: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
