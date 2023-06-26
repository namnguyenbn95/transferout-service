package vn.vnpay.dbinterface.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.vnpay.dbinterface.entity.LuckyAccNumPromotion;
import vn.vnpay.dbinterface.entity.LuckyAccNumType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LuckyAccNumPromotionRepository extends JpaRepository<LuckyAccNumPromotion, Long> {
    List<LuckyAccNumPromotion> findByStatus(String status);
}
