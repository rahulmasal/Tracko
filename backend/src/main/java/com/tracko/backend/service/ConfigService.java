package com.tracko.backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tracko.backend.exception.DuplicateResourceException;
import com.tracko.backend.exception.ResourceNotFoundException;
import com.tracko.backend.model.AppConfig;
import com.tracko.backend.repository.AppConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ConfigService {

    private final AppConfigRepository appConfigRepository;
    private final ObjectMapper objectMapper;

    @Cacheable(value = "appConfig", key = "#configKey")
    public String getConfigValue(String configKey) {
        return appConfigRepository.findByConfigKey(configKey)
            .map(AppConfig::getConfigValue)
            .orElse(null);
    }

    public <T> T getConfigAs(String configKey, Class<T> type) {
        String value = getConfigValue(configKey);
        if (value == null) return null;
        try {
            return objectMapper.readValue(value, type);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse config value for key: " + configKey, e);
        }
    }

    public List<AppConfig> getAllConfig() {
        return appConfigRepository.findAll();
    }

    @CacheEvict(value = "appConfig", key = "#configKey")
    @Transactional
    public AppConfig updateConfig(String configKey, Object value, String description,
                                   String module, Long updatedBy) {
        String jsonValue;
        if (value instanceof String) {
            jsonValue = (String) value;
        } else {
            try {
                jsonValue = objectMapper.writeValueAsString(value);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to serialize config value", e);
            }
        }

        AppConfig config = appConfigRepository.findByConfigKey(configKey)
            .orElse(AppConfig.builder()
                .configKey(configKey)
                .build());

        config.setConfigValue(jsonValue);
        if (description != null) config.setDescription(description);
        if (module != null) config.setModule(module);
        config.setUpdatedBy(updatedBy);

        return appConfigRepository.save(config);
    }

    @CacheEvict(value = "appConfig", key = "#configKey")
    @Transactional
    public void deleteConfig(String configKey) {
        AppConfig config = appConfigRepository.findByConfigKey(configKey)
            .orElseThrow(() -> new ResourceNotFoundException("AppConfig", "configKey", configKey));
        appConfigRepository.delete(config);
    }
}
