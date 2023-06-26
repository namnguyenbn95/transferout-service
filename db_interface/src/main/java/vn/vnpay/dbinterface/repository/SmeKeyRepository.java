package vn.vnpay.dbinterface.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.vnpay.dbinterface.entity.SmeKey;

@Repository
public interface SmeKeyRepository extends JpaRepository<SmeKey, Long> {

}
