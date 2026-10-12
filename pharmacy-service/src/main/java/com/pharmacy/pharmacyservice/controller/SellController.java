package com.pharmacy.pharmacyservice.controller;

import com.pharmacy.pharmacyservice.dto.request.SellRequestDTO;
import com.pharmacy.pharmacyservice.dto.response.SellResponseDTO;
import com.pharmacy.pharmacyservice.service.SellService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/sell")
@RequiredArgsConstructor
public class SellController {

    private final SellService sellService;

    @PostMapping
    public ResponseEntity<SellResponseDTO> sellMedicine(
            @Valid @RequestBody SellRequestDTO request) {
        return ResponseEntity.ok(sellService.sellMedicine(request));
    }
}