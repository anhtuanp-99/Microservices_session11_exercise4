package com.pharmacy.pharmacyservice.dto.message;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Message gửi qua Redis Pub/Sub channel pharmacy-alerts.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class PharmacyAlertMessage implements Serializable {

    private String type;        // IMPORT, EXPORT, LOW_STOCK, EXPIRED
    private String message;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;
}