package vn.vnpay.dbinterface.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "MB_DEPOSIT_TERM")
public class MbDepositTerm implements Serializable {
    @Id
    @Column(name = "ID")
    private int id;

    @Column(name = "DEPOSIT_TERM_CODE")
    private String depositTermCode;

    @Column(name = "DEPOSIT_TERM_NAME")
    private String depositTermName;

    @Column(name = "DEPOSIT_TERM_NAME_EN")
    private String depositTermNameEn;

    @Column(name = "DEPOSIT_PRODUCT_CODE")
    private String depositProductCode;

    @Column(name = "DEPOSIT_PRODUCT_NAME")
    private String depositProductName;

    @Column(name = "INTEREST_RATE_CODE")
    private String interestRateCode;

    @Column(name = "INTEREST_RATE_VALUE")
    private String interestRateValue;

    @Column(name = "ORDER_ID")
    private String orderId;

    @Column(name = "FLEXIBLE_DEPOSIT")
    private String flexibleDeposit;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "DEPOSIT_TERM_UNIT")
    private String depositTermUnit;

    @Column(name = "DEPOSIT_TERM_VALUE")
    private Long depositTermValue;
}
