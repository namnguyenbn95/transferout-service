package vn.vnpay.dbinterface.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.vnpay.dbinterface.entity.MbServiceDisplayHome;

@Repository
public interface MbServiceDisplayHomeRepository extends JpaRepository<MbServiceDisplayHome, Long> {
}
