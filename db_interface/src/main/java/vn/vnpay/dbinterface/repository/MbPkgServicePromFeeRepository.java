package vn.vnpay.dbinterface.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.vnpay.dbinterface.entity.MbPkgServicePromFee;

import java.util.List;

@Repository
public interface MbPkgServicePromFeeRepository extends JpaRepository<MbPkgServicePromFee, Long> {

    List<MbPkgServicePromFee> findByPromCodeAndServiceCodeAndStatusAndCcy(
            String promCode, String serviceCode, String status, String ccy);
}
