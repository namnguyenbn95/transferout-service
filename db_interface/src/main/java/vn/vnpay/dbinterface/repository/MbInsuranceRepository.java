package vn.vnpay.dbinterface.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.vnpay.dbinterface.entity.MbInsurance;

import java.util.List;

@Repository
public interface MbInsuranceRepository extends JpaRepository<MbInsurance, Long> {
    List<MbInsurance> findByStatus(String status);

    List<MbInsurance> findByStatusAndInsurancePayer(String status, String payer);
}
