package com.pharmacy.pharmacyservice.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicineRequestDTO {

    @NotBlank(message = "Tên thuốc không được để trống")
    @Size(max = 200)
    private String name;

    @NotBlank(message = "Nhà sản xuất không được để trống")
    private String manufacturer;

    @NotNull(message = "Đơn giá không được để trống")
    @Min(value = 1, message = "Đơn giá phải lớn hơn 0")
    private BigDecimal unitPrice;

    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 0, message = "Số lượng không được âm")
    private Integer stockQuantity;

    @Future(message = "Ngày hết hạn phải ở tương lai")
    private LocalDate expiryDate;

    @Size(max = 1000)
    private String description;
}