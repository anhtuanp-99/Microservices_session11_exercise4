package com.pharmacy.pharmacyservice.dto.response;

import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicineResponseDTO implements Serializable {   // cần Serializable cho cache
    private Long id;
    private String name;
    private String manufacturer;
    private BigDecimal unitPrice;
    private Integer stockQuantity;
    private LocalDate expiryDate;
    private String description;
}