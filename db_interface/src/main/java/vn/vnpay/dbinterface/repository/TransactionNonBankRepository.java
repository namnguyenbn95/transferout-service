package vn.vnpay.dbinterface.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import vn.vnpay.dbinterface.entity.TransactionNonBank;

@Repository
@Transactional(propagation = Propagation.REQUIRED)
public interface TransactionNonBankRepository extends JpaRepository<TransactionNonBank, Long> {
}
