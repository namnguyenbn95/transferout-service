package vn.vnpay.dbinterface.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import vn.vnpay.dbinterface.entity.SmeTrans;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface SmeTransRepository extends JpaRepository<SmeTrans, Long>, JpaSpecificationExecutor<SmeTrans> {
    List<SmeTrans> findByTranxTimeBetweenAndCifNo(LocalDateTime from, LocalDateTime to, String cifNo);

    List<SmeTrans> findByTranxTimeBetweenAndCifInt(LocalDateTime from, LocalDateTime to, int cifNo);

    List<SmeTrans> findByTranxTimeBetweenAndCreatedUserAndCifNo(LocalDateTime from, LocalDateTime to, String createdUser, String cifNo);

    List<SmeTrans> findByTranxTimeBetweenAndFromAccAndStatus(LocalDateTime from, LocalDateTime to, String fromAcc, String status);

    List<SmeTrans> findByStatus(String status);

    @Query(value = "select st.* from sme_trans st " +
            "            where st.TRANX_TIME between ?1 and ?2 " +
            "            and st.CIF_NO = ?3", nativeQuery = true)
    List<SmeTrans> findByCheckerUser(LocalDateTime from, LocalDateTime to, String cifNo);

    @Modifying
    @Query("update SmeTrans s set s.status = :status, s.approvedDate = current_timestamp where s.id = :transId")
    void updateTransStatus(Long transId, String status);

    @Modifying
    @Query("update SmeTrans s set s.status = :status, s.approvedDate = current_timestamp, s.approvedUser = :approveUser where s.id in :transId")
    void updateTransStatusBatch(List<Long> transId, String status, String approveUser);

    @Modifying
    @Query("update SmeTrans s set s.status = :status, s.reason = :reason, s.approvedUser = :approveUser, s.approvedDate = current_timestamp where s.id = :transId")
    void updateTransStatusAndReasonAndApproveUser(Long transId, String status, String reason, String approveUser);

    @Modifying
    @Query("update SmeTrans s set s.status = :status, s.reason = :reason, s.approvedUser = :approveUser, s.approvedDate = current_timestamp where s.id in :transId")
    void updateTransStatusAndReasonAndApproveUserBatch(List<Long> transId, String status, String reason, String approveUser);

    @Query(value = "select sme_trans_seq.nextval from dual", nativeQuery = true)
    BigDecimal getTranSeqNextVal();

    List<SmeTrans> findAllByIdIn(List<Long> ids);

    @Query(value = "SELECT sme_trans_seq.nextval FROM dual", nativeQuery = true)
    BigDecimal getNextValSmeTransSeq();

    List<SmeTrans> findAllByBatchId(String id);

    List<SmeTrans> findAllByBatchIdAndStatus(String id, String stauts);

    @Query(value = "select count(tranx_id) from sme_trans where batch_id = ?1", nativeQuery = true)
    int getNumOfTranByBatchId(String batchId);

    @Query(value = "select count(tranx_id) from sme_trans where batch_id = ?1 and " +
            "tranx_status in ?2", nativeQuery = true)
    int getNumOfTranByBatchIdAndListStatus(String batchId, List<String> statuses);

    @Query(value = "select distinct batch_id from sme_trans where tranx_id in ?1", nativeQuery = true)
    List<String> getListBatchIdByTranId(List<String> transIds);

    Optional<SmeTrans> findByTranxTimeBetweenAndTsolRef(LocalDateTime from, LocalDateTime to, String tsolRef);

    Optional<SmeTrans> findByTsolRef(String tsolRef);
}
