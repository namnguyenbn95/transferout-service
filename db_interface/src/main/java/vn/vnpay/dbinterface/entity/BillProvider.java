package vn.vnpay.dbinterface.entity;

import lombok.Getter;
import lombok.Setter;
import vn.vnpay.dbinterface.entity.pcm.BillField;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;

@Entity
@Getter
@Setter
@Table(name = "MB_BILL_PROVIDERS")
public class BillProvider implements Serializable {
    @Id
    @Column(name = "BILL_PROVIDER_CODE")
    String billProviderCode;

    @Column(name = "BILL_PROVIDER_NAME")
    String billProviderName;

    @Column(name = "BILL_PROVIDER_NAME_EN")
    String billProviderNameEn;

    @Column(name = "BILL_SERVICE_CODE")
    String billServiceCode;

    @Column(name = "STATUS")
    String status;

    @Column(name = "PROVIDER_AUTO_DEBIT")
    String providerAutoDebit;

    @Column(name = "SERVICETYPE_CODE")
    String serviceType;

    @Column(name = "SERVICE_CODE")
    String serviceCode;

    @Column(name = "SUPPLIER_CODE")
    String supplierCode;

    @Column(name = "ORDER_NUMBER")
    Integer orderNumber;

    @Column(name = "PROVIDER_ACCOUNT")
    String providerAccount;

    @Column(name = "TELLER_ID")
    String tellerId;

    @Column(name = "FEE_TRANS")
    BigDecimal feeTrans;

    @Column(name = "FEE_CODE")
    String feeCode;

    @Column(name = "MAX_LIMIT")
    BigDecimal maxLimit;

    @Column(name = "MIN_LIMIT")
    BigDecimal minLimit;

    @Column(name = "IS_AUTO_DEBIT_MB")
    String isAutoDebitMB;

    @Column(name = "IS_AUTO_DEBIT_IB")
    String isAutoDebitIB;

    @Column(name = "IS_ALLOW_PAY_MB")
    String isPayMB;

    @Column(name = "IS_ALLOW_PAY_IB")
    String isPayIB;

    @Column(name = "IS_BILLER_DIVISION_CAPTURE_MODE")
    String isBillerDivisionCaptureMode;

    @Column(name = "IS_WITH_BILLS")
    String isWithBills;

    @Column(name = "IS_ADHOC_PMT")
    String isAdhocPmt;

    @Transient
    String companyCode;

    @Transient
    ArrayList<BillSubProvider> subProviders;

    @Transient
    ArrayList<BillField> billFields;

    @Transient
    String pmtRestriction;

    @Transient
    String isPayerCharge;
}
