package vn.vnpay.dbinterface.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.vnpay.dbinterface.entity.batch_salary.BatchFile;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SalaryBatchFileRepository extends JpaRepository<BatchFile, Long> {

    List<BatchFile> findByStatus(String status);

    List<BatchFile> findByCreatedDateBetweenAndCifNo(LocalDateTime fromDate,
                                                     LocalDateTime toDate,
                                                     String cifNo);

    List<BatchFile> findByIdIn(List<Long> ids);
}
