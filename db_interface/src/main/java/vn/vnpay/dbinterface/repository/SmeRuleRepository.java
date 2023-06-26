package vn.vnpay.dbinterface.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.vnpay.dbinterface.entity.SmeRule;

import java.util.List;
import java.util.Optional;

@Repository
public interface SmeRuleRepository extends JpaRepository<SmeRule, Long> {
    Optional<SmeRule> findByServiceCodeAndStatus(String serviceCode, String status);

    List<SmeRule> findByStatusAndServiceCodeIn(String status, List<String> listServiceCode);
}
