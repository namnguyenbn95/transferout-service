package vn.vnpay.dbinterface.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.vnpay.dbinterface.entity.SmeCustomer;

import java.util.Optional;

public interface SmeCustomerRepository extends JpaRepository<SmeCustomer, Long> {
    Optional<SmeCustomer> findByCusId(long cusId);

    Optional<SmeCustomer> findByCif(String cif);
}
