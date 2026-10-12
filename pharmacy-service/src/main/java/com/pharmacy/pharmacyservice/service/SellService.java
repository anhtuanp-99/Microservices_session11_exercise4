package com.pharmacy.pharmacyservice.service;

import com.pharmacy.pharmacyservice.dto.request.SellRequestDTO;
import com.pharmacy.pharmacyservice.dto.response.SellResponseDTO;
import com.pharmacy.pharmacyservice.entity.Medicine;
import com.pharmacy.pharmacyservice.exception.ResourceNotFoundException;
import com.pharmacy.pharmacyservice.repository.MedicineRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class SellService {

    private static final String LOCK_KEY_PREFIX = "lock:medicine:";
    private static final long WAIT_TIME_SECONDS = 3;
    private static final long LEASE_TIME_SECONDS = 5;

    private final MedicineRepository medicineRepository;
    private final RedissonClient redissonClient;

    /**
     * Bán thuốc với Distributed Lock.
     * <p>
     * LUỒNG:
     *   1. Lấy lock theo key "lock:medicine:{id}"
     *      - tryLock(3, 5, SECONDS): chờ tối đa 3s để acquire, giữ lock tối đa 5s
     *   2. Vào vùng critical (chỉ 1 thread tại 1 thời điểm):
     *      a. Đọc stock từ DB
     *      b. Kiểm tra đủ hàng không
     *      c. Trừ stock
     *      d. Lưu DB
     *   3. Giải phóng lock
     * <p>
     * Nếu không acquire được lock trong 3s → trả về "hệ thống đang xử lý".
     */
    public SellResponseDTO sellMedicine(SellRequestDTO request) {
        String threadName = Thread.currentThread().getName();
        Long medicineId = request.getMedicineId();
        String lockKey = LOCK_KEY_PREFIX + medicineId;

        RLock lock = redissonClient.getLock(lockKey);

        boolean locked = false;
        try {
            log.info("[{}] Đang cố acquire lock: {}", threadName, lockKey);

            // ===== BƯỚC 1: LẤY LOCK =====
            locked = lock.tryLock(WAIT_TIME_SECONDS, LEASE_TIME_SECONDS, TimeUnit.SECONDS);

            if (!locked) {
                log.warn("[{}] Không lấy được lock sau {}s — có thread khác đang xử lý",
                        threadName, WAIT_TIME_SECONDS);
                return SellResponseDTO.builder()
                        .success(false)
                        .message("Hệ thống đang xử lý đơn hàng này, vui lòng thử lại sau")
                        .medicineId(medicineId)
                        .threadName(threadName)
                        .build();
            }

            log.info("[{}] ✅ Đã acquire lock: {}", threadName, lockKey);

            // ===== BƯỚC 2: VÙNG CRITICAL =====
            return processSale(request, threadName);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("[{}] Bị gián đoạn khi chờ lock: {}", threadName, e.getMessage());
            return SellResponseDTO.builder()
                    .success(false)
                    .message("Yêu cầu bị gián đoạn")
                    .medicineId(medicineId)
                    .threadName(threadName)
                    .build();

        } finally {
            // ===== BƯỚC 3: GIẢI PHÓNG LOCK =====
            // Chỉ release nếu thread hiện tại đang giữ lock
            if (locked && lock.isHeldByCurrentThread()) {
                lock.unlock();
                log.info("[{}] 🔓 Đã release lock: {}", threadName, lockKey);
            }
        }
    }

    /**
     * Xử lý bán hàng trong vùng critical.
     */
    private SellResponseDTO processSale(SellRequestDTO request, String threadName) {
        Long medicineId = request.getMedicineId();
        Integer quantity = request.getQuantity();

        // Bước 2a: Đọc DB (sau khi đã có lock → đọc giá trị mới nhất)
        Medicine medicine = medicineRepository.findById(medicineId)
                .orElseThrow(() -> new ResourceNotFoundException("Medicine", "ID", medicineId));

        log.info("[{}] Stock hiện tại của medicineId={}: {}",
                threadName, medicineId, medicine.getStockQuantity());

        // Bước 2b: Kiểm tra đủ hàng
        if (medicine.getStockQuantity() < quantity) {
            log.warn("[{}] ❌ Không đủ hàng: cần {}, còn {}",
                    threadName, quantity, medicine.getStockQuantity());

            return SellResponseDTO.builder()
                    .success(false)
                    .message("Hết hàng. Chỉ còn " + medicine.getStockQuantity() + " sản phẩm")
                    .medicineId(medicineId)
                    .remainingStock(medicine.getStockQuantity())
                    .threadName(threadName)
                    .build();
        }

        // Bước 2c + 2d: Trừ stock + lưu
        medicine.setStockQuantity(medicine.getStockQuantity() - quantity);
        medicineRepository.save(medicine);

        log.info("[{}] ✅ Bán thành công: {} × {} — stock còn lại: {}",
                threadName, medicine.getName(), quantity, medicine.getStockQuantity());

        return SellResponseDTO.builder()
                .success(true)
                .message("Bán thuốc thành công")
                .medicineId(medicineId)
                .soldQuantity(quantity)
                .remainingStock(medicine.getStockQuantity())
                .threadName(threadName)
                .build();
    }
}