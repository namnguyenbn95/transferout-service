package vn.vnpay.dbinterface.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.vnpay.dbinterface.entity.SmeClientHis;

@Repository
public interface SmeClientHisRepository extends JpaRepository<SmeClientHis, Long> {

}
