package vn.vnpay.dbinterface.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.vnpay.dbinterface.entity.SmeCustomerUserActionHis;

@Repository
public interface SmeCustomerUserActionHisRepository extends JpaRepository<SmeCustomerUserActionHis, Long> {
}
