package vn.vnpay.dbinterface.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.vnpay.dbinterface.entity.MbDepositTerm;

import java.util.List;
import java.util.Optional;

@Repository
public interface MbDepositTermRepository extends JpaRepository<MbDepositTerm, Integer> {
    List<MbDepositTerm> findByStatus(String status);
    Optional<MbDepositTerm> findByDepositTermCode(String termCode);
}
