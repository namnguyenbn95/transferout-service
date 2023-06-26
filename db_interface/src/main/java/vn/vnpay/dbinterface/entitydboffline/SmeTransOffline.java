package vn.vnpay.dbinterface.entitydboffline;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@DynamicUpdate
@Table(name = "sme_trans")
public class SmeTransOffline {
    @Id
    @Column(name = "tranx_id")
    private Long id;

    @Column(name = "tranx_type")
    private String tranxType;

    @Column(name = "created_user")
    private String createdUser;

    @Column(name = "created_mobile")
    private String createdMobile;

    @Column(name = "CHECKER_AUTHEN_TYPE")
    private String checkerAuthenType;

    @Column(name = "approved_user")
    private String approvedUser;

    @Column(name = "approved_date")
    private LocalDateTime approvedDate;

    @Column(name = "approved_mobile")
    private String approvedMobile;

    @Column(name = "from_acc")
    private String fromAcc;

    @Column(name = "to_acc")
    private String toAcc;

    @Column(name = "provider_code")
    private String providerCode;

    @Column(name = "service_code")
    private String serviceCode;

    @Column(name = "amount")
    private double amount;

    @Column(name = "tranx_note")
    private String tranxNote;

    @Column(name = "tranx_status")
    private String status;

    @Column(name = "tranx_time")
    private LocalDateTime tranxTime;

    @Column(name = "res_bank_code")
    private String resBankCode;

    @Column(name = "res_bank_desc")
    private String resBankDesc;

    @Column(name = "flat_fee")
    private double flatFee;

    @Column(name = "fee_on_amt")
    private double feeOnAmt;

    @Column(name = "TOTAL_AMOUNT")
    private double totalAmount;

    @Column(name = "ccy")
    private String ccy;

    @Column(name = "tranx_remark")
    private String tranxRemark;

    @Column(name = "product_type")
    private String productType;

    @Column(name = "tranx_refno")
    private String tranxRefno;

    @Column(name = "BENE_BRANCH_CODE")
    private String beneBranchCode;

    @Column(name = "BRANCH_CODE")
    private String branchCode;

    @Column(name = "service_type")
    private String serviceType;

    @Column(name = "FEE_TYPE")
    private String feeType;

    @Column(name = "credit_name")
    private String creditName;

    @Column(name = "BENE_BANK_CODE")
    private String beneBankCode;

    @Column(name = "CUS_NAME")
    private String cusName;

    @Column(name = "cif_no")
    private String cifNo;

    @Column(name = "cif_int")
    private Integer cifInt;

    @Column(name = "MAKER_AUTHEN_TYPE")
    private String makerAuthenType;

    @Column(name = "TRANX_CONTENT")
    private String tranxContent;

    @Column(name = "metadata")
    private String metadata;

    @Column(name = "CHANNEL")
    private String channel;

    @Column(name = "REASON")
    private String reason;

    @Column(name = "SOURCE_PAYMENT")
    private String sourcePayment;

    @Column(name = "DEBIT_BRANCH_CODE")
    private String debitBranchCode;

    @Column(name = "REAL_AMOUNT")
    private BigDecimal realAmount;

    @Column(name = "ACC_NO_SELECT")
    private String accNoSelect;

    @Column(name = "TELLER")
    private String teller;

    @Column(name = "TSOL_REF")
    private String tsolRef;

    @Transient
    private String authenType;

    @Transient
    private String challenge;
}
