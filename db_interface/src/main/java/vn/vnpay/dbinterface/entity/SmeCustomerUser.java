package vn.vnpay.dbinterface.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import vn.vnpay.dbinterface.dto.AccountDTO;
import vn.vnpay.dbinterface.dto.CardDTO;
import vn.vnpay.dbinterface.dto.DebitCardDTO;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@DynamicUpdate
@Table(name = "SME_CUSTOMERS_USERS")
public class SmeCustomerUser {

    @Id
    @Column(name = "CUS_USER_ID")
    private long cusUserId;

    @Column(name = "CUS_ID")
    private long cusId;

    @Column(name = "CUS_NAME")
    private String cusName;

    @Column(name = "USER_NAME")
    private String username;

    @Column(name = "PIN")
    private String pin;

    @Column(name = "PIN_TYPE")
    private String pinType;

    @Column(name = "CUS_STATUS")
    private String cusStatus;

    @Column(name = "CUS_USER_STATUS")
    private String cusUserStatus;

    @Column(name = "MOBILE_OTP")
    private String mobileOtp;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "CIF_CORE")
    private String cif;

    @Column(name = "CIF_INT")
    private int cifInt;

    @Column(name = "LAST_LOGIN_SOURCE")
    private String lastLoginSource;

    @Column(name = "REGIST_CODE")
    private String registCode;

    @Column(name = "AUTO_UNLOCK_TIME")
    private LocalDateTime autoUnlockTime;

    @Column(name = "ACTIVED_DATE")
    private LocalDateTime activedDate;

    @Column(name = "PIN_EXPIRE_DATE")
    private LocalDateTime pinExpireDate;

    @Column(name = "LAST_LOGIN_DATE")
    private LocalDateTime lastLoginDate;

    @Column(name = "ROLE_TYPE")
    private String roleType;

    @Column(name = "AUTHEN_NOTIFY")
    private String authenNotify;

    @Column(name = "DEFAULT_ACC")
    private String defaultAcc;

    @Column(name = "CUS_USER_PREV_STATUS")
    private String previousStatus;

    @Column(name = "REGIST_CODE_SMART_OTP")
    private String registCodeSmartOtp;

    @Column(name = "REGIST_CODE_SMART_OTP_EXPIRE")
    private LocalDateTime registCodeSmartOtpExpire;

    @Column(name = "authen_method")
    private String authenMethod;

    @Column(name = "ACTIVATED_SMART_OTP")
    private String activatedSmartOtp;

    @Column(name = "package_code")
    private String packageCode;

    @Column(name = "PROMOTION_CODE")
    private String promotionCode;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "BRANCH_CODE")
    private String branchCode;

    @Column(name = "POS_CODE")
    private String posCode;

    @Column(name = "REGIST_CHANNEL")
    private String registChannel;

    @Column(name = "SERIAL_NUMBER")
    private String serialNumber;

    @Column(name = "REGIST_BILL_RECEIVE")
    private String registBillReceive;

    @Column(name = "BALANCE_NOTIFY")
    private String balanceNotify;

    @Column(name = "LOCK_LOGIN_WEB")
    private String lockLoginWeb;

    @Column(name = "DEFAULT_ACC_ALIAS")
    private String defaultAccAlias;

    @Column(name = "TIME_REGISTER_OTT")
    private LocalDateTime timeRegisterOTT;

    @Column(name = "HARDTOKEN_STATUS")
    private String hardTokenStatus;

    @Transient
    private String confirmType;

    @Transient
    private String validPromCode;

    @Transient
    private String taxCode;

    @Transient
    private List<AccountDTO> listAccount = new ArrayList<>();

    @Transient
    private List<CardDTO> listCreditCard = new ArrayList<>();

    @Transient
    private List<CardDTO> listSmeCard = new ArrayList<>();

    @Transient
    private List<DebitCardDTO> listDebitCard = new ArrayList<>();

    @Transient
    private String sigCustType;

    @Transient
    private String vatExemptFlag;

    @Transient
    private String accountPkgCode;

    @Transient
    private List<LimitTransEntity> listTransLimit = new ArrayList<>();
}
