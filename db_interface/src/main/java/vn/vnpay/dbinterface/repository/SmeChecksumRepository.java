package vn.vnpay.dbinterface.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.vnpay.dbinterface.entity.SmeChecksum;

import java.util.Optional;

@Repository
public interface SmeChecksumRepository extends JpaRepository<SmeChecksum, Long> {
    Optional<SmeChecksum> findByUsername(String username);
}
