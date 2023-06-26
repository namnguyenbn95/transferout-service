package vn.vnpay.dbinterface.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import vn.vnpay.dbinterface.entity.SmeTransactionDetail;

public interface SmeTransDetailRepository extends JpaRepository<SmeTransactionDetail, Long> {
}
