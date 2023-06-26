package vn.vnpay.dbinterface.entity.batch_salary;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "SME_TRANSBATCH_DETAIL")
public class BatchItem {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BATCH_ITEM_SEQ")
    @SequenceGenerator(name = "BATCH_ITEM_SEQ", sequenceName = "BATCH_ITEM_SEQ", allocationSize = 1)
    @Column(name = "ID")
    Long id;
    @Column(name = "NUMBER_ITEM")
    Long numberItem;

    @Column(name = "FILE_ID")
    Long batchId;

    @Column(name = "BENE_ACC")
    String beneAcc;

    @Column(name = "BENE_BANK")
    String beneBank;

    @Column(name = "AMOUNT")
    BigDecimal amount;

    @Column(name = "CCY")
    String ccy;

    @Column(name = "FEE")
    BigDecimal fee;

    @Column(name = "VAT")
    BigDecimal vat;

    @Column(name = "STATUS")
    String status;

    @Column(name = "BENE_NAME")
    String beneName;

    @Column(name = "ID_NUMBER")
    String idNumber;

    @Column(name = "ISSUED_DATE")
    String issuedDate;

    @Column(name = "ISSUED_PLACE")
    String issuedPlace;

    @Column(name = "CONTENT")
    String content;

    @Column(name = "REF_NO")
    String refNo;

    @Column(name = "CONTENT_ERR")
    String contentErr;

    @Column(name = "TELLER")
    String teller;

    @Column(name = "SEQUENCE")
    String sequence;

    @Column(name = "HOST_DATE")
    String hostDate;

    @Column(name = "PC_TIME")
    String pcTime;

    @Column(name = "BANK_CODE")
    String bankCode;

    @Column(name = "BANK_NAME")
    String bankName;

    @Column(name = "TRANS_TYPE")
    String transType;

    @Column(name = "CHANNEL")
    String channelType;

    @Column(name = "FW_BRANCH")
    String fwBrn;

    @Column(name = "BENE_CODE")
    String beneCode;

    @Column(name = "ACC_NO")
    String accNo;

    @Column(name = "ACC_TYPE")
    String beneAccType;

    @Column(name = "ACC_BRANCH")
    String beneAccBranch;

    @Column(name = "ACC_NAME")
    String accName;

    @Column(name = "RECEIVE_CODE")
    String receiveCode;

    @Column(name = "RECEIVE_BANK")
    String receiveBank;

    @Column(name = "BEN_BANK_NAME")
    String beneBankName;

    @Column(name = "METADATA")
    String metaData;

    @Column(name = "CONTENT_ERR_EN")
    String contentErrEN;

    @Column(name = "REMARK")
    String remark;

    @Column(name = "ID_NUMBER_TYPE")
    String idNumberType;

    @Transient
    String beneBranchCode;

    @Transient
    String beneBranchName;

    @Transient
    String beneCityCode;

    @Transient
    String beneCityName;

    @Transient
    String beneBankCode;

    @Transient
    boolean isSucc;

    public BigDecimal getFee() {
        return this.fee == null ? BigDecimal.ZERO : this.fee;
    }

    public BigDecimal getVat() {
        return this.vat == null ? BigDecimal.ZERO : this.vat;
    }

}
