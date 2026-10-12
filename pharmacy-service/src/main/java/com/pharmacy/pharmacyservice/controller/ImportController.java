package com.pharmacy.pharmacyservice.controller;

import com.pharmacy.pharmacyservice.publisher.PharmacyAlertPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/imports")
@RequiredArgsConstructor
public class ImportController {

    private final PharmacyAlertPublisher publisher;

    /**
     * Demo API nhập hàng → gửi alert tới Redis Pub/Sub.
     * POST /api/v1/imports?medicineName=Panadol&quantity=100
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> importMedicine(
            @RequestParam String medicineName,
            @RequestParam Integer quantity) {

        // Trong thực tế: update DB, ghi log nhập hàng, ...
        // Ở đây chỉ demo Pub/Sub.

        String alertMessage = String.format("Đã nhập %d hộp %s",
                quantity, medicineName);

        publisher.publishAlert("IMPORT", alertMessage);

        return ResponseEntity.ok(Map.of(
                "status", "SUCCESS",
                "message", alertMessage
        ));
    }
}