package vn.vnpay.dbinterface.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import vn.vnpay.dbinterface.entity.SmeUserAccRole;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional(propagation = Propagation.REQUIRED)
public interface SmeUserAccRoleRepository extends JpaRepository<SmeUserAccRole, Long> {

    List<SmeUserAccRole> findByCusUserId(long cusUserId);

    Optional<SmeUserAccRole> findByCusUserIdAndAccNo(long cusUserId, String accNo);

    @Modifying
    @Query("UPDATE SmeUserAccRole SET status = :status WHERE cusUserId = :cusUserId")
    void updateStatusByCusUserId(@Param(value = "status") String status, @Param(value = "cusUserId") long cusUserId);

    List<SmeUserAccRole> findByCusUserIdAndAccType(long cusUserId, String accType);
}
