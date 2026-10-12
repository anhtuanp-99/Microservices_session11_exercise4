package com.pharmacy.pharmacyservice.controller;

import com.pharmacy.pharmacyservice.dto.request.MedicineRequestDTO;
import com.pharmacy.pharmacyservice.dto.response.MedicineResponseDTO;
import com.pharmacy.pharmacyservice.service.MedicineService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/medicines")
@RequiredArgsConstructor
public class MedicineController {

    private final MedicineService medicineService;

    @PostMapping
    public ResponseEntity<MedicineResponseDTO> createMedicine(
            @Valid @RequestBody MedicineRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(medicineService.createMedicine(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedicineResponseDTO> getMedicineById(@PathVariable Long id) {
        return ResponseEntity.ok(medicineService.getMedicineById(id));
    }

    @GetMapping
    public ResponseEntity<List<MedicineResponseDTO>> getAllMedicines() {
        return ResponseEntity.ok(medicineService.getAllMedicines());
    }

    @PutMapping("/{id}")
    public ResponseEntity<MedicineResponseDTO> updateMedicine(
            @PathVariable Long id,
            @Valid @RequestBody MedicineRequestDTO request) {
        return ResponseEntity.ok(medicineService.updateMedicine(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMedicine(@PathVariable Long id) {
        medicineService.deleteMedicine(id);
        return ResponseEntity.noContent().build();
    }
}