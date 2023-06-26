package vn.vnpay.dbinterface.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.vnpay.dbinterface.entity.FeeReportEntity;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface FeeReportRepository extends JpaRepository<FeeReportEntity, Long> {

    @Query(value = "SELECT " +
            "                       st.flat_fee fee, st.fee_on_amt vat, (st.flat_fee + st.fee_on_amt) feeVat, " +
            "                      st.tranx_id, st.amount, st.tranx_time, st.fee_type, st.from_acc, st.TOTAL_AMOUNT, " +
            "                   st.ccy, st.metadata, st.tranx_type, " +
            "                          row_number() over (ORDER BY st.tranx_time DESC) line_number " +
            "                        FROM sme_trans st " +
            "                       where st.from_acc = ?1 and st.tranx_time between ?2 and ?3 and st.TRANX_STATUS = '3'", nativeQuery = true)
    List<FeeReportEntity> getListFeeReport(String fromAcc, Timestamp fromDate, Timestamp toDate);

//    @Query(value = "select count(*) from sme_trans st " +
//            "where  st.from_acc = ?1 and st.tranx_time BETWEEN ?2 and ?3 and st.TRANX_STATUS = '3'", nativeQuery = true)
//    int getAllTotalRecord(String accNo, Timestamp from, Timestamp to);
}
