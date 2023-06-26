package vn.vnpay.dbinterface.reposotorydboffline;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import vn.vnpay.dbinterface.entitydboffline.BatchFileOffline;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@Transactional
public interface SalaryBatchFileRepositoryOffline extends JpaRepository<BatchFileOffline, Long> {
    List<BatchFileOffline> findByCreatedDateBetweenAndCifNo(LocalDateTime fromDate,
                                                            LocalDateTime toDate,
                                                            String cifNo);
}
