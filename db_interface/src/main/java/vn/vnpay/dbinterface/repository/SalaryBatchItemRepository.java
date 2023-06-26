package vn.vnpay.dbinterface.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import vn.vnpay.dbinterface.entity.batch_salary.BatchItem;

import java.util.List;


@Repository
public interface SalaryBatchItemRepository extends JpaRepository<BatchItem, Long> {
    List<BatchItem> findByBatchIdAndStatus(Long batchId, String status);

    List<BatchItem> findByBatchId(long batchId);

    List<BatchItem> findByBatchIdAndStatusIn(Long batchId, List<String> status);

    List<BatchItem> findAllByIdInAndBatchId(List<Long> items, Long batchId);

    void deleteById(Long id);

    @Transactional
    @Modifying
    @Query(value = " UPDATE SME_TRANSBATCH_DETAIL a SET a.STATUS = :status WHERE a.REF_NO = :refNo   ", nativeQuery = true)
    void updateListBatchItemStatusByRefNo(@Param("status") String status, @Param("refNo") String refNo);

    @Transactional
    @Modifying
    @Query(value = " UPDATE SME_TRANSBATCH_DETAIL a SET a.STATUS = :status WHERE a.FILE_ID = :fileID   ", nativeQuery = true)
    void updateListBatchItemStatusByFileId(@Param("status") String status, @Param("fileID") Long fileID);

}
