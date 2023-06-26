package vn.vnpay.dbinterface.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.vnpay.dbinterface.entity.SmeCheck;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface SmeCheckRepository extends JpaRepository<SmeCheck, Long> {
    Optional<SmeCheck> findByUsernameAndCheckType(String username, String checkType);

    void deleteByUsernameAndCheckType(String username, String checkType);

    void deleteByUsernameAndCheckTypeAndCreatedDateLessThan(String username, String checkType, LocalDateTime createdDate);
}
