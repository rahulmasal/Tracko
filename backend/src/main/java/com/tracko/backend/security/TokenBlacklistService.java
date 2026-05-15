package com.tracko.backend.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

    private final StringRedisTemplate redisTemplate;
    private static final String BLACKLIST_PREFIX = "token:blacklist:";

    public void blacklist(String token, long expirationMs) {
        String key = BLACKLIST_PREFIX + token;
        redisTemplate.opsForValue().set(key, "1", expirationMs, TimeUnit.MILLISECONDS);
        log.debug("Token blacklisted");
    }

    public boolean isBlacklisted(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(BLACKLIST_PREFIX + token));
    }
}
