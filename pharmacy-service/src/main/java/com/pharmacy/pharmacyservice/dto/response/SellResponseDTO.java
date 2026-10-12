package com.pharmacy.pharmacyservice.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SellResponseDTO {
    private boolean success;
    private String message;
    private Long medicineId;
    private Integer soldQuantity;
    private Integer remainingStock;
    private String threadName;
}