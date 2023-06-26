package vn.vnpay.dbinterface.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "MB_PACKAGE_PROMOTIONS")
public class MbPkgProm implements Serializable {

    @Id
    @Column(name = "PROMOTION_CODE")
    private String promCode;

    @Column(name = "PROMOTION_NAME")
    private String promName;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "PACKAGE_CODE")
    private String pkgCode;

    @Column(name = "PROMOTION_TYPE")
    private String promType;

    @Column(name = "DESCRIPTION")
    private String desc;

    @Column(name = "PERIOD")
    private Integer period;

    @Column(name = "VALID_DATE")
    private LocalDateTime validDate;

    @Column(name = "EXPIRED_DATE")
    private LocalDateTime expiredDate;

    @Column(name = "BRANCHES")
    private String branches;

}
