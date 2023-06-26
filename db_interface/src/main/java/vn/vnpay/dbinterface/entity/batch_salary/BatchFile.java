package vn.vnpay.dbinterface.entity.batch_salary;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "SME_TRANSBATCH_FILE")
public class BatchFile implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "batch_file_seq")
    @SequenceGenerator(name = "batch_file_seq", sequenceName = "batch_file_seq", allocationSize = 1)
    @Column(name = "FILE_ID")
    Long id;

    @Column(name = "FILE_NAME")
    String fileName;

    @Column(name = "CREATED_DATE")
    LocalDateTime createdDate;

    @Column(name = "CREATED_USER")
    String createdUser;

    @Column(name = "CONFIRM_DATE")
    LocalDateTime confirmDate;

    @Column(name = "CONFIRM_USER")
    String approveUser;

    @Column(name = "FROM_ACC")
    String fromAcc;

    @Column(name = "TOTAL_AMOUNT")
    BigDecimal totalAmount;

    @Column(name = "TOTAL_AMOUNT_IN")
    BigDecimal totalAmountIn;

    @Column(name = "TOTAL_AMOUNT_OUT")
    BigDecimal totalAmountOut;

    @Column(name = "TOTAL_AMOUNT_ID")
    BigDecimal totalAmountID;

    @Column(name = "CCY")
    String ccy;

    @Column(name = "TOTAL_FEE")
    BigDecimal totalFee;

    @Column(name = "STATUS")
    String status;

    @Column(name = "FILE_PATH")
    String filePath;

    @Column(name = "CIF_NO")
    String cifNo;

    @Column(name = "BATCH_TYPE")
    String batchType;

    @Column(name = "CONTENT")
    String content;

    @Column(name = "METADATA")
    String metaData;

    @Column(name = "REF_NO")
    String refNo;

    @Column(name = "TRANS_DATE")
    String transDate;

    @Column(name = "FEE_TYPE")
    String feeType;

    @Column(name = "SOURCE_BRANCH")
    Integer debitAccBranch;

    @Column(name = "ACC_TYPE")
    String accType;

    @Column(name = "ACC_BRN")
    Integer accBrn;

    @Column(name = "ACC_NAME")
    String accName;

    @Column(name = "REMARK1")
    String remark1;

    @Column(name = "TOTAL_ITEM")
    Integer totalItem;

    @Column(name = "REASON")
    String reason;

    @Column(name = "TRANX_TIME")
    LocalDateTime tranxTime;

    @Column(name = "SEQ_BANK")
    String seqBank;

    @Transient
    String paymentGoal;

    @Transient
    String authenType;

    @Transient
    String challenge;

    @Transient
    private boolean requestProcessed;

}
