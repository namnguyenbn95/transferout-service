package vn.vnpay.dbinterface.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@DynamicUpdate
@EntityListeners(AuditingEntityListener.class)
@Table(name = "sme_trans_nonbank")
public class TransactionNonBank {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idsme_trans_nonbank_seq")
    @SequenceGenerator(name = "idsme_trans_nonbank_seq", sequenceName = "sme_trans_nonbank_seq", allocationSize = 1)
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "exec_user")
    private String execUser;

    @Column(name = "AUTHEN_METHOD")
    private String authenMethod;

    @Column(name = "CIF")
    private String cif;

    @Column(name = "USERNAME")
    private String username;

    @Column(name = "ROLE_TYPE")
    private String roleType;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "MOBILE")
    private String mobile;

    @Column(name = "SOURCE")
    private String source;

    @Column(name = "OS")
    private String os;

    @Column(name = "OV")
    private String ov;

    @Column(name = "PM")
    private String pm;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "TRANS_TYPE")
    private String transType;

    @Column(name = "ACCOUNT_NO")
    private String accountNo;

    @Column(name = "SERVICE_CODE")
    private String serviceCode;

    @Column(name = "PROVIDER_CODE")
    private String providerCode;

    @Column(name = "MIN_LIMIT_TRANS")
    private Double minLimitTrans;

    @Column(name = "MAX_LIMIT_TRANS")
    private Double maxLimitTrans;

    @Column(name = "DAY_LIMIT_VND")
    private Double dayLimitVnd;

    @Column(name = "DAY_LIMIT_USD")
    private Double dayLimitUsd;

    @Column(name = "OLD_EMAIL")
    private String oldEmail;

    @Column(name = "NEW_EMAIL")
    private String newEmail;

    @Column(name = "RECEIPT_EMAIL_TYPE")
    private String receiptEmailType;

    @Column(name = "REMARK")
    private String remark;

    @Column(name = "BRANCH_CODE")
    private String branchCode;

    @Column(name = "CUS_ID_NUMBER")
    private String cusIdNumber;

    @Column(name = "ACCOUNT_TYPE")
    private String accountType;

    @Column(name = "CIF_INT")
    private Integer cifInt;

    @Column(name = "SERVICE_TYPE")
    private String serviceType;

    @CreatedDate
    @Column(name = "CREATED_DATE", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @Column(name = "SERVICE_NAME")
    private String serviceName;

    @Column(name = "CRD_NBR")
    private String crdNbr;

    @Column(name = "CRD_TYPE")
    private String crdType;

    @Column(name = "CRD_STT")
    private String crdStt;

    @Column(name = "ACCOUNT_NEW")
    private String accountNew;

    @Column(name = "PROVIDER_NAME")
    private String providerName;

    @Column(name = "LIST_EMAIL_REMIND_DEBIT")
    private String lstEmailRemindDebit;

    @Column(name = "LIST_PHONE_REMIND_DEBIT")
    private String lstPhoneRemindDebit;

    @Column(name = "RES_CODE")
    private String resBankCode;

    @Column(name = "RES_DESC")
    private String resBankDesc;

    @Column(name = "METADATA")
    private String metadata;

}
