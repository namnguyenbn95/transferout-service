package vn.vnpay.dbinterface.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.vnpay.dbinterface.entity.LuckyAccNumType;

import java.util.List;

@Repository
public interface LuckyAccNumTypeRepository extends JpaRepository<LuckyAccNumType, String> {
    List<LuckyAccNumType> findByStatusOrderByOrderNo(String status);

    List<LuckyAccNumType> findByStatusAndDisplayInListOrderByOrderNo(String status,String display);
}
