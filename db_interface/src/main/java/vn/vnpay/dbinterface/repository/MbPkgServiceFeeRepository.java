package vn.vnpay.dbinterface.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.vnpay.dbinterface.entity.MbPkgServiceFee;

import java.util.List;

@Repository
public interface MbPkgServiceFeeRepository extends JpaRepository<MbPkgServiceFee, Long> {
    List<MbPkgServiceFee> findByPkgCodeAndServiceCodeAndStatusAndCcy(
            String pkgCode, String serviceCode, String status, String ccy);
}
