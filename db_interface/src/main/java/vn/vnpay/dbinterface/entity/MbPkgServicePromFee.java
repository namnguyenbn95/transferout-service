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
@Table(name = "MB_PACKAGE_PROMOTION_SERVICE_FEES")
public class MbPkgServicePromFee implements Serializable {
    @Id
    @Column(name = "PACKAGE_PROMOTION_SERVICE_FEE_ID")
    long promFeeId;

    @Column(name = "PROMOTION_CODE")
    String promCode;

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

    public int getFromHour() {
        return fromHour == null ? 0 : fromHour;
    }

    public int getToHour() {
        return toHour == null ? 24 : toHour;
    }
}
