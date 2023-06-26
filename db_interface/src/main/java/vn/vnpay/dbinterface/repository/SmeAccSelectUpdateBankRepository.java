package vn.vnpay.dbinterface.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.vnpay.dbinterface.entity.SmeAccSelectUpdateBank;

import java.util.List;

@Repository
public interface SmeAccSelectUpdateBankRepository extends JpaRepository<SmeAccSelectUpdateBank, Long> {
    @Modifying
    @Query("update SmeAccSelectUpdateBank s set s.updatedDate = current_timestamp , s.statusUpdate = :status , s.count = s.count + 1 where s.id = :transId")
    void updateStatusCoreBank(@Param("transId") Long id, @Param("status") String status);

    List<SmeAccSelectUpdateBank> findByStatusUpdateIn(List<String> status);

    List<SmeAccSelectUpdateBank> findByStatusUpdateInAndCountLessThan(List<String> status,Integer limit);

}
