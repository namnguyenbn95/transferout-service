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
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "MB_ACCOUNT_SERVICE_PACKAGE")
public class MbAccountSerPkg implements Serializable {

    @Id
    @Column(name = "PACKAGE_CODE")
    private String pkgCode;

    @Column(name = "PACKAGE_NAME_VN")
    private String pkgName;

    @Column(name = "PACKAGE_NAME_EN")
    private String pkgNameEn;

    @Column(name = "PROMOTION_CODE")
    private String promotionCode;

    @Column(name = "ORDER_NO")
    Integer orderNo;

    @Column(name = "PACKAGE_LEVEL")
    Integer pkgLevel;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "URL_ICON_PACKAGE")
    private String urlIconPkg;

    @Column(name = "DEBIT_CARD_QUANTITY")
    private Integer debitCardQuantity;

    @Column(name = "CREDIT_CARD_QUANTITY")
    private Integer creditCardQuantity;

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

    @Column(name = "PACKAGE_INFO_VN")
    private String pkgInfo;

    @Column(name = "PACKAGE_INFO_EN")
    private String pkgInfoEn;

    @Column(name = "PACKAGE_FEE_VN")
    private String pkgFee;

    @Column(name = "PACKAGE_FEE_EN")
    private String pkgFeeEn;

    @Column(name = "PROMO_EXPIRY_VN")
    private String promoExpiry;

    @Column(name = "PROMO_EXPIRY_EN")
    private String promoExpiryEn;

    @Column(name = "PROMO_CONDITIONS_VN")
    private String promoConditions;

    @Column(name = "PROMO_CONDITIONS_EN")
    private String promoConditionsEn;

    @Column(name = "GET_PROMOTION")
    private String isPromotion;

    @Column(name = "AVERAGE_BALANCE_FOR_FREE")
    private Long avgBalForFree;
}
