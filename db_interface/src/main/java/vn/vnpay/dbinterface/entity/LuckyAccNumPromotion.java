package vn.vnpay.dbinterface.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "MB_LUCKY_ACCOUNT_NUMBER_PROMOTION")
public class LuckyAccNumPromotion implements Serializable {
    @Id
    @Column(name = "ID")
    long id;

    @Column(name = "SERVICE_CODE")
    String serviceCode;

    @Column(name = "CUSTOMER_LEVEL")
    String customerLevel;

    @Column(name = "ACCOUNT_SERVICE_CODE")
    String accServiceCode;

    @Column(name = "LUCKY_ACCOUNT_NUMBER_TYPE_CODE")
    String luckyAccNumTypeCode;

    @Column(name = "NUMBER_LAST_DIGIT")
    String numberLastDigit;

    @Column(name = "PROMOTION_TYPE")
    String promotionType;

    @Column(name = "PROMOTION_VALUE")
    BigDecimal promotionValue;

    @Column(name = "MIN_VALUE_PAYMENT")
    Integer minValuePayment;

    @Column(name = "MAX_VALUE_PAYMENT")
    Integer maxValuePayment;

    @Column(name = "FROM_AMOUNT")
    Integer fromAmount;

    @Column(name = "TO_AMOUNT")
    Integer toAmount;

    @Column(name = "FROM_DATE")
    private LocalDateTime fromDate;

    @Column(name = "TO_DATE")
    private LocalDateTime toDate;

    @Column(name = "BELONG_TO_ACCOUNT_PACKAGE")
    String belongToAccPkg;

    @Column(name = "STATUS")
    String status;

    @CreatedDate
    @Column(name = "CREATED_DATE", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(name = "UPDATED_DATE")
    private LocalDateTime updatedDate;

    @Column(name = "CREATED_USER")
    String createdUser;

    @Column(name = "UPDATED_USER")
    String updatedUser;

}
