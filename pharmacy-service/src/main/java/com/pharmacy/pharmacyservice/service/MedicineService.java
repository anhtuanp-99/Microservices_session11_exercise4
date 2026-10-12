package com.pharmacy.pharmacyservice.service;

import com.pharmacy.pharmacyservice.dto.request.MedicineRequestDTO;
import com.pharmacy.pharmacyservice.dto.response.MedicineResponseDTO;
import com.pharmacy.pharmacyservice.entity.Medicine;
import com.pharmacy.pharmacyservice.exception.ResourceNotFoundException;
import com.pharmacy.pharmacyservice.mapper.MedicineMapper;
import com.pharmacy.pharmacyservice.repository.MedicineRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MedicineService {

    private final MedicineRepository medicineRepository;
    private final MedicineMapper medicineMapper;

    /**
     * Tạo mới — KHÔNG cache vì là write operation.
     */
    public MedicineResponseDTO createMedicine(MedicineRequestDTO request) {
        Medicine medicine = Medicine.builder()
                .name(request.getName())
                .manufacturer(request.getManufacturer())
                .unitPrice(request.getUnitPrice())
                .stockQuantity(request.getStockQuantity())
                .expiryDate(request.getExpiryDate())
                .description(request.getDescription())
                .build();

        Medicine saved = medicineRepository.save(medicine);
        log.info("Đã tạo medicine id={}", saved.getId());
        return medicineMapper.toResponseDTO(saved);
    }

    /**
     * Lấy chi tiết thuốc — CÓ CACHE.
     * <p>
     * @Cacheable hoạt động:
     *   - Lần 1: cache MISS → chạy method → query DB → lưu vào Redis key "medicines::1"
     *   - Lần 2: cache HIT → KHÔNG chạy method → trả về từ Redis
     * <p>
     * Key: "medicines::1" (tự ghép từ cache name + key)
     * Value: MedicineResponseDTO dạng JSON
     */
    @Cacheable(value = "medicines", key = "#id")
    public MedicineResponseDTO getMedicineById(Long id) {
        log.info(">>> [DB QUERY] Lấy medicine id={} từ DATABASE (không phải cache)", id);

        // Thread.sleep để mô phỏng query chậm — thấy rõ cache hit nhanh hơn
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        Medicine medicine = medicineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medicine", "ID", id));

        return medicineMapper.toResponseDTO(medicine);
    }

    /**
     * Lấy tất cả — có thể cache nếu muốn. Ở đây không cache vì list có thể lớn.
     */
    public List<MedicineResponseDTO> getAllMedicines() {
        log.info(">>> [DB QUERY] Lấy tất cả medicines từ DATABASE");
        return medicineMapper.toResponseDTOList(medicineRepository.findAll());
    }

    /**
     * Cập nhật — dùng @CacheEvict để xóa dữ liệu cũ
     */
    @CacheEvict(value = "medicines", key = "#id")
    public MedicineResponseDTO updateMedicine(Long id, MedicineRequestDTO request) {
        Medicine medicine = medicineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medicine", "ID", id));

        medicine.setName(request.getName());
        medicine.setManufacturer(request.getManufacturer());
        medicine.setUnitPrice(request.getUnitPrice());
        medicine.setStockQuantity(request.getStockQuantity());
        medicine.setExpiryDate(request.getExpiryDate());
        medicine.setDescription(request.getDescription());

        Medicine updated = medicineRepository.save(medicine);
        log.info("Đã cập nhật medicine id={}, cache đã bị xóa", id);
        return medicineMapper.toResponseDTO(updated);
    }

    /**
     * Xóa — dùng @CacheEvict để xóa cache tương ứng.
     */
    @CacheEvict(value = "medicines", key = "#id")
    public void deleteMedicine(Long id) {
        if (!medicineRepository.existsById(id)) {
            throw new ResourceNotFoundException("Medicine", "ID", id);
        }
        medicineRepository.deleteById(id);
        log.info("Đã xóa medicine id={}, cache đã được clear", id);
    }
}