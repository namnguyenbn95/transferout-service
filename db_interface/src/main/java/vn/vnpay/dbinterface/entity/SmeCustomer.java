package vn.vnpay.dbinterface.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "SME_CUSTOMERS")
public class SmeCustomer {

    @Id
    @Column(name = "CUS_ID")
    private long cusId;

    @Column(name = "CONFIRM_TYPE")
    private String confirmType;

    @Column(name = "REGIST_CHANNEL")
    private String registChannel;

    @Column(name = "CIF_CORE")
    private String cif;

    @Column(name = "BUSINESS_TYPE")
    private String businessType;

    @Column(name = "CUS_NAME")
    private String cusName;

    @Column(name = "ESTABLISHED_DATE")
    private LocalDateTime establishedDate;

    @Column(name = "CUS_IDNUMBER")
    private String cusIdNumber;

    @Column(name = "ISSUEDATE")
    private LocalDateTime issueDate;

    @Column(name = "ISSUEPLACE")
    private String issuePlace;

    @Column(name = "CUS_TYPE")
    private String cusType;

    @Column(name = "CUS_GROUP")
    private String cusGroup;

    @Column(name = "CUS_EMAIL")
    private String cusEmail;

    @Column(name = "CUS_ADDR")
    private String cusAddr;

    @Column(name = "BRANCH_CODE")
    private String branchCode;

    @Column(name = "POS_CODE")
    private String posCode;

    @Column(name = "DEFAULT_ACC")
    private String defaultAcc;

    @Column(name = "PACKAGE_CODE")
    private String packageCode;

    @Column(name = "PROMOTION_CODE")
    private String promotionCode;

    @Column(name = "CUS_STATUS")
    private String cusStatus;

    @Column(name = "CREATED_DATE")
    private LocalDateTime createdDate;

    @Column(name = "CONFIRM_DATE")
    private LocalDateTime confirmDate;

    @Column(name = "CREATED_USER")
    private String createdUser;

    @Column(name = "CONFIRM_USER")
    private String confirmUser;

    @Column(name = "UPDATE_DATE")
    private LocalDateTime updateDate;

    @Column(name = "UPDATE_USER")
    private String updateUser;

    @Column(name = "CONFIRM_BRANCH")
    private String confirmBranch;

    @Column(name = "UPDATE_BRANCH")
    private String updateBranch;

    @Column(name = "MOBILE_NO")
    private String mobileNo;

    @Column(name = "BRANCH_CODE_CIF")
    private String branchCodeCif;

    @Column(name = "CIF_INT")
    private Integer cifInt;

    @Column(name = "DEFAULT_ACC_ALIAS")
    private String defaultAccAlias;
}
