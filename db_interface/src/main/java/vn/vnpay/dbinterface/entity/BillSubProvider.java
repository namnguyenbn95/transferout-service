package vn.vnpay.dbinterface.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Table(name = "MB_BILL_SUB_PROVIDERS")
public class BillSubProvider implements Serializable {
    @Id
    @Column(name = "BILL_SUB_PROVIDER_CODE")
    String billSubProviderCode;

    @Column(name = "BILL_SUB_PROVIDER_NAME")
    String billSubProviderName;

    @Column(name = "BILL_SUB_PROVIDER_NAME_EN")
    String billSubProviderNameEn;

    @Column(name = "SUB_PROVIDER_AUTO_DEBIT")
    String billSubProviderAutoDebit;

    @Column(name = "PREFIX_NUMBER")
    String prefixNumber;

    @Column(name = "CREDIT_ACCOUNT")
    String creditAccount;

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

    @Column(name = "STATUS")
    String status;

    @Column(name = "BILL_PROVIDER_CODE")
    String billProviderCode;
}
