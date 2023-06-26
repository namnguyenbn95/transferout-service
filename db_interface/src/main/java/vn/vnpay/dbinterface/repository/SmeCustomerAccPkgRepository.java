package vn.vnpay.dbinterface.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import vn.vnpay.dbinterface.entity.SmeCustomerAccPkg;

import java.util.List;
import java.util.Optional;

@Repository
public interface SmeCustomerAccPkgRepository extends JpaRepository<SmeCustomerAccPkg, Long> {

    Optional<SmeCustomerAccPkg> findByCusId(long cusId);

    @Transactional(rollbackFor = Exception.class)
    void deleteByCusId(long cusId);

}
