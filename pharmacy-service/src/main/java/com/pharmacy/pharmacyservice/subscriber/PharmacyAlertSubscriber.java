package com.pharmacy.pharmacyservice.subscriber;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pharmacy.pharmacyservice.dto.message.PharmacyAlertMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PharmacyAlertSubscriber {

    private final ObjectMapper objectMapper;

    /**
     * Method được gọi tự động khi có message trên channel pharmacy-alerts.
     * <p>
     * Signature: nhận String (raw JSON) → parse thành Object.
     * Tên method phải khớp với MessageListenerAdapter("onMessage").
     */
    public void onMessage(String jsonMessage) {
        try {
            PharmacyAlertMessage alert = objectMapper.readValue(
                    jsonMessage, PharmacyAlertMessage.class);

            log.info("┌─ [SUBSCRIBER] Nhận alert từ channel pharmacy-alerts");
            log.info("│  Type      : {}", alert.getType());
            log.info("│  Message   : {}", alert.getMessage());
            log.info("│  Timestamp : {}", alert.getTimestamp());
            log.info("└─ [DASHBOARD] Hiển thị cảnh báo lên màn hình quản lý");

            // Trong thực tế: đẩy xuống WebSocket để hiển thị real-time trên dashboard
            // notifyWebSocket(alert);

        } catch (Exception e) {
            log.error("Lỗi parse message: {}", e.getMessage(), e);
        }
    }
}