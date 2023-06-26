package vn.vnpay.dbinterface.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.vnpay.dbinterface.entity.OttKeyEntity;

import java.util.Optional;

@Repository
public interface OttKeyRepository extends JpaRepository<OttKeyEntity, Long> {

    Optional<OttKeyEntity> findByMobileNo(String mobileNo);
}
