package com.pharmacy.pharmacyservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pharmacy.pharmacyservice.config.RedisPubSubConfig;
import com.pharmacy.pharmacyservice.dto.message.PharmacyAlertMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class PharmacyAlertPublisher {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    /**
     * Gửi message tới channel pharmacy-alerts.
     * Fire-and-forget: không chờ response, không lưu lại.
     */
    public void publishAlert(String type, String message) {
        PharmacyAlertMessage alert = PharmacyAlertMessage.builder()
                .type(type)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();

        try {
            String json = objectMapper.writeValueAsString(alert);

            Long receivers = redisTemplate.convertAndSend(
                    RedisPubSubConfig.CHANNEL, json);

            log.info(">>> Đã publish tới channel [{}]: {} (số subscriber nhận: {})",
                    RedisPubSubConfig.CHANNEL, json, receivers);

        } catch (JsonProcessingException e) {
            log.error(">>> Lỗi serialize message: {}", e.getMessage(), e);
        }
    }
}