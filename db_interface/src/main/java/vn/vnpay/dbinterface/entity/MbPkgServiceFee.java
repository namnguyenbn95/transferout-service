package vn.vnpay.dbinterface.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "MB_PACKAGE_SERVICE_FEES")
public class MbPkgServiceFee implements Serializable {
    @Id
    @Column(name = "PACKAGE_SERVICE_FEE_ID")
    long feeId;

    @Column(name = "PACKAGE_CODE")
    String pkgCode;

    @Column(name = "SERVICE_CODE")
    String serviceCode;

    @Column(name = "METHOD_OTP")
    String methodOtp;

    @Column(name = "FEE_TYPE")
    String feeType;

    @Column(name = "FEE_VALUE")
    BigDecimal feeValue;

    @Column(name = "VAT")
    Long vat;

    @Column(name = "MIN_FEE")
    BigDecimal minFee;

    @Column(name = "MAX_FEE")
    BigDecimal maxFee;

    @Column(name = "FROM_AMOUNT")
    BigDecimal fromAmount;

    @Column(name = "TO_AMOUNT")
    BigDecimal toAmount;

    @Column(name = "FROM_HOUR")
    Integer fromHour;

    @Column(name = "TO_HOUR")
    Integer toHour;

    @Column(name = "STATUS")
    String status;

    @Column(name = "CURRENCY")
    String ccy;

    public Integer getFromHour() {
        return fromHour == null ? 0 : fromHour;
    }

    public Integer getToHour() {
        return toHour == null ? 24 : toHour;
    }

    public BigDecimal getFromAmount() {
        return fromAmount == null ? BigDecimal.ZERO : fromAmount;
    }

    public BigDecimal getToAmount() {
        return toAmount == null ? BigDecimal.ZERO : toAmount;
    }
}
