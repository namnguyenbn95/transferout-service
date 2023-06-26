package vn.vnpay.dbinterface.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.vnpay.dbinterface.entity.SmeCustomerAccPkgVipAcc;

import java.util.List;

@Repository
public interface SmeCustomerAccPkgVipAccRepository extends JpaRepository<SmeCustomerAccPkgVipAcc, Long> {
    List<SmeCustomerAccPkgVipAcc> findByCusIdAndAccPkgCodeAndIsCurrent(long cusId, String accPkgCode, String isCurrent);

    void deleteByCusIdAndAccPkgCode(long cusId, String accPkgCode);

    @Modifying
    @Query("UPDATE SmeCustomerAccPkgVipAcc SET isCurrent = :isCurrent WHERE cusId = :cusId")
    void updateIsCurrentByCusId(@Param(value = "isCurrent") String isCurrent, @Param(value = "cusId") long cusId);
}
