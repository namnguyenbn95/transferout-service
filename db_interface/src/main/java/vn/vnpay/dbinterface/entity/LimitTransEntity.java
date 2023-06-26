package vn.vnpay.dbinterface.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

@Getter
@Setter
@Entity
public class LimitTransEntity {
    @Id
    @Column(name = "PACKAGE_SUB_SERVICE_TYPE_LIMIT_ID")
    private String pkgSubServiceTypeLimitId;

    @Column(name = "SERVICETYPE_CODE")
    private String serviceTypeCode;

    @Column(name = "CURRENCY")
    private String currency;

    @Column(name = "LIMIT_CUSTOMER")
    private long limitCus;

    @Column(name = "DEFAULT_LIMIT")
    private long defaultLimit;

    @Column(name = "PACKAGE_CODE")
    private String packageCode;

    @Column(name = "SUB_SERVICE_CODE")
    private String subServiceCode;

    @Transient
    private String typeLimit; //1:lấy từ service type limit

    @Transient
    private long limitCusHis;

    @Transient
    private String isChange;

}
