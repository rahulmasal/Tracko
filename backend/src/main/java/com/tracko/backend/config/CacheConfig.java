package com.tracko.backend.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

        cacheConfigurations.put("appConfig", RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofHours(1)).disableCachingNullValues());
        cacheConfigurations.put("users", RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(30)).disableCachingNullValues());
        cacheConfigurations.put("attendance", RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(5)).disableCachingNullValues());
        cacheConfigurations.put("visits", RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(5)).disableCachingNullValues());
        cacheConfigurations.put("scores", RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofHours(2)).disableCachingNullValues());

        return RedisCacheManager.builder(connectionFactory)
            .cacheDefaults(RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10)))
            .withInitialCacheConfigurations(cacheConfigurations)
            .build();
    }
}
