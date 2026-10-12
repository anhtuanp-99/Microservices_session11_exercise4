package com.pharmacy.pharmacyservice.mapper;

import com.pharmacy.pharmacyservice.dto.response.MedicineResponseDTO;
import com.pharmacy.pharmacyservice.entity.Medicine;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MedicineMapper {

    public MedicineResponseDTO toResponseDTO(Medicine medicine) {
        if (medicine == null) return null;
        return MedicineResponseDTO.builder()
                .id(medicine.getId())
                .name(medicine.getName())
                .manufacturer(medicine.getManufacturer())
                .unitPrice(medicine.getUnitPrice())
                .stockQuantity(medicine.getStockQuantity())
                .expiryDate(medicine.getExpiryDate())
                .description(medicine.getDescription())
                .build();
    }

    public List<MedicineResponseDTO> toResponseDTOList(List<Medicine> medicines) {
        if (medicines == null) return List.of();
        return medicines.stream().map(this::toResponseDTO).toList();
    }
}